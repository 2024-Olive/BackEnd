package com.cj.olive.domain.Heartbeat.service;

import com.cj.olive.domain.Heartbeat.entity.Heartbeat;
import com.cj.olive.domain.Heartbeat.error.HeartbeatErrorCode;
import com.cj.olive.domain.Heartbeat.model.SearchTypeEnum;
import com.cj.olive.domain.Heartbeat.repository.HeartbeatRepository;
import com.cj.olive.domain.User.entity.User;
import com.cj.olive.domain.User.repository.UserRepository;
import com.cj.olive.domain.User.service.UserService;
import com.cj.olive.global.error.GlobalErrorCode;
import com.cj.olive.global.error.exception.AppException;
import com.cj.olive.presentation.dto.res.Heartbeat.HeartbeatAllItemResDto;
import com.cj.olive.presentation.dto.res.Heartbeat.HeartbeatAllResDto;
import com.cj.olive.presentation.dto.res.Heartbeat.HeartbeatSearchResDto;
import com.cj.olive.presentation.dto.res.Heartbeat.HeartbeatStaticResDto;
import com.cj.olive.presentation.dto.res.User.UserHeartbeatResDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.temporal.WeekFields;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import static com.cj.olive.global.util.DateUtil.*;

@Slf4j
@RequiredArgsConstructor
@Service
public class HeartbeatService {

    private final HeartbeatRepository heartbeatRepository;
    private final UserRepository userRepository;
    private final UserService userService;
    private final Map<Long, List<Integer>> userHeartRateData = new ConcurrentHashMap<>();

    public void cacheHeartbeatPerSecond(int heartRate, User user) {
        userHeartRateData.computeIfAbsent(user.getId(), k -> new ArrayList<>()).add(heartRate);

        // 임계치 초과 확인 및 알림
        if (heartRate >= user.getThreshold()) {
            // TODO: 임계치 초과 알림 로직
        }
    }

    // TODO: Redis 를 통해 캐시 처리
    @Scheduled(fixedRate = 600000)
    public void saveHeartBeat() {
        List<Heartbeat> heartbeatsToSave = new ArrayList<>();

        userHeartRateData.forEach((userId, realTimeData) -> {
            if (realTimeData.isEmpty()) return;

            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new AppException(GlobalErrorCode.USER_NOT_FOUND));

            Heartbeat heartbeat = createHeartbeat(user, realTimeData);
            heartbeatsToSave.add(heartbeat);
            realTimeData.clear();
        });

        if (!heartbeatsToSave.isEmpty()) {
            heartbeatRepository.saveAll(heartbeatsToSave);
        }
    }

    // 자신 심박수 가져오기
    public HeartbeatSearchResDto getUserHeartbeatStats(String searchTypeEnum, LocalDate date) {
        User requestUser = userService.getRequestUser();
        return getSearchResult(validateSearchType(searchTypeEnum), requestUser, date);
    }


    // 관리자가 회원 전체 날짜별 심박수 가져오기
    public List<HeartbeatAllResDto> getDailyHeartbeatStats() {
        // 관리자 권한 체크
        User requestUser = userService.getRequestUser();
        if (!requestUser.isAdmin()) {
            throw new AppException(GlobalErrorCode.ONLY_ADMIN_ACCESS);
        }

        // 리스트 받아오기
        List<Object[]> rawData = heartbeatRepository.findHeartbeatStatsGroupedByUserAndDate();

        // 결과값 존재 여부 체크
        if (rawData.isEmpty()) {
            return null;
        }

        // 날짜별 데이터 매핑 진행
        Map<LocalDate, List<HeartbeatAllItemResDto>> groupedByDate = new HashMap<>();

        for (Object[] row : rawData) {
            LocalDate date = ((java.sql.Date) row[0]).toLocalDate();
            Integer minBpm = (Integer) row[1];
            Integer maxBpm = (Integer) row[2];
            String username = (String) row[3];

            // HeartbeatAllItemResDto 객체 item 생성
            HeartbeatAllItemResDto item = new HeartbeatAllItemResDto(minBpm, maxBpm, username);

            // date - item 쌍 저장
            groupedByDate.computeIfAbsent(date, k -> new ArrayList<>()).add(item);
        }

        // 엔트리셋을 스트림으로 변환하여 각 엔트리를 HeartbeatAllResDto 로 변환 후, 반환
        return groupedByDate.entrySet().stream()
                .map(entry -> new HeartbeatAllResDto(
                        formatDate(entry.getKey().atStartOfDay()),
                        entry.getValue()))
                .sorted(Comparator.comparing(HeartbeatAllResDto::getDate).reversed())
                .collect(Collectors.toList());
    }

    // 사용자 이름으로 심박수 가져오기
    public HeartbeatSearchResDto getAdminHeartbeatStats(String username, String searchTypeEnum, LocalDate date) {
        User requestUser = userService.getRequestUser();
        if (!requestUser.isAdmin()) {
            throw new AppException(GlobalErrorCode.ONLY_ADMIN_ACCESS);
        }

        User searchUser = userRepository.findByUsername(username)
                .orElseThrow(() -> new AppException(GlobalErrorCode.USER_NOT_FOUND));

        return getSearchResult(validateSearchType(searchTypeEnum), searchUser, date);
    }

    // 검색 타입에 따른 메소드 호출
    public HeartbeatSearchResDto getSearchResult(SearchTypeEnum searchTypeEnum, User user, LocalDate date) {

        return switch (searchTypeEnum) {
            case DAY -> getDailyStats(user, date);
            case WEEK -> getWeeklyStats(user, date);
            case MONTH -> getMonthlyStats(user, date);
        };
    }

    // 일별 검색
    private HeartbeatSearchResDto getDailyStats(User user, LocalDate date) {
        // 날짜 준비 및 유효성 검사
        validateDate(date);
        LocalDateTime startOfDay = date.atStartOfDay();
        LocalDateTime endOfDay = startOfDay.plusDays(1);

        // 사용자의 해당 날짜의 시간별 평균 심박수 데이터 조회
        List<HeartbeatStaticResDto> staticData =
                heartbeatRepository.findHourlyAvgBpmByUserAndDate(user, startOfDay, endOfDay);

        // 데이터 존재 여부 검사
        if (staticData.isEmpty()) {
            return null;
        }

        // 데이터 매핑을 위한 임시 데이터
        Map<Integer, HeartbeatStaticResDto> resultMap = new HashMap<>();
        // 시간 별 데이터 매핑
        staticData.forEach(dto -> resultMap.put(Integer.parseInt(dto.getDateTime()), dto));

        // 결과값을 반환할 데이터
        List<HeartbeatStaticResDto> results = new ArrayList<>();
        // 24 시간 동안의 데이터 생성
        for (int i = 0; i < 24; i++) {
            if (resultMap.containsKey(i)) { // 데이터 존재
                results.add(resultMap.get(i));
            } else { // 데이터 미존재
                results.add(new HeartbeatStaticResDto(String.valueOf(i), 0.0, 0, 0));
            }
        }

        // 결과 dto 생성
        return buildHeartbeatResDto(user, date, results);
    }

    // 주별 검색
    private HeartbeatSearchResDto getWeeklyStats(User user, LocalDate date) {
        // 날짜 준비 및 유효성 검사
        LocalDate endDate = date.plusDays(7);
        validateDateRange(date, endDate);

        // 사용자의 해당 주의 요일별 평균 심박수 데이터 조회
        List<HeartbeatStaticResDto> staticData = heartbeatRepository.findDailyAvgBpmInWeekByUserAndDate(user, date.atStartOfDay(), endDate.atStartOfDay());

        // 데이터 존재 여부 검사
        if (staticData.isEmpty()) {
            return null;
        }

        // 데이터 매핑을 위한 임시 데이터
        Map<LocalDate, HeartbeatStaticResDto> resultMap = new HashMap<>();
        // 데이터를 요일로 매핑
        staticData.forEach(dto -> resultMap.put(parseDate(dto.getDateTime()), dto));

        // 결과값을 위한 임시 데이터
        List<HeartbeatStaticResDto> results = new ArrayList<>();
        // 1주일 동안의 데이터 생성
        for (LocalDate d = date; !d.isAfter(endDate); d = d.plusDays(1)) {
            if (resultMap.containsKey(d)) { // 데이터 존재
                results.add(resultMap.get(d));
            } else { // 데이터 미존재
                results.add(new HeartbeatStaticResDto(formatDate(d.atStartOfDay()), 0.0, 0, 0));
            }
        }

        // 결과 dto 생성
        return buildHeartbeatResDto(user, date, results);
    }

    // 월별 검색
    private HeartbeatSearchResDto getMonthlyStats(User user, LocalDate date) {
        // 날짜 준비 및 유효성 검사
        YearMonth yearMonth = YearMonth.of(date.getYear(), date.getMonth());
        LocalDate firstDayOfMonth = yearMonth.atDay(1);
        LocalDate lastDayOfMonth = yearMonth.atEndOfMonth();
        validateDateRange(firstDayOfMonth, lastDayOfMonth);

        // 사용자의 해당 월의 주별 평균 심박수 데이터 조회
        List<HeartbeatStaticResDto> staticData = heartbeatRepository.findWeeklyAvgBpmInMonthByUserAndDate(user, firstDayOfMonth.atStartOfDay(), lastDayOfMonth.plusDays(1).atStartOfDay());

        // 데이터 존재 여부 검사
        if (staticData.isEmpty()) {
            return null;
        }

        // 매핑을 위한 임시 데이터
        Map<Double, HeartbeatStaticResDto> resultMap = new HashMap<>();

        // 주 단위로 매핑
        staticData.forEach(dto -> resultMap.put(Double.parseDouble(dto.getDateTime()), dto));

        // 결과를 위한 데이터
        List<HeartbeatStaticResDto> results = new ArrayList<>();
        // 월의 주 단위를 계산 (한 달을 4주로 가정하지 않음)
        WeekFields weekFields = WeekFields.of(Locale.getDefault());
        int endWeek = lastDayOfMonth.get(weekFields.weekOfWeekBasedYear());

        for (double i = 1; i <= endWeek; i++) {
            if (resultMap.containsKey(i)) { // 데이터 존재
                results.add(resultMap.get(i));
            } else { // 데이터 미존재
                results.add(new HeartbeatStaticResDto(String.valueOf(i), 0.0, 0, 0));
            }
        }

        // 결과 dto 생성
        return buildHeartbeatResDto(user, firstDayOfMonth, results);
    }

    // 심박수 생성
    private Heartbeat createHeartbeat(User user, List<Integer> realTimeData) {
        int sum = realTimeData.stream().mapToInt(Integer::intValue).sum();
        int max = realTimeData.stream().mapToInt(Integer::intValue).max().orElse(Integer.MIN_VALUE);
        int min = realTimeData.stream().mapToInt(Integer::intValue).min().orElse(Integer.MAX_VALUE);
        int avg = sum / realTimeData.size();

        return Heartbeat.builder()
                .avgBpm(avg)
                .maxBpm(max)
                .minBpm(min)
                .user(user)
                .build();
    }

    // 반환 값 생성하기
    private HeartbeatSearchResDto buildHeartbeatResDto(User user, LocalDate date, List<HeartbeatStaticResDto> staticData) {
        int minBpm = (int) staticData.stream().mapToDouble(HeartbeatStaticResDto::getAvgBpm).min().orElse(0);
        int maxBpm = (int) staticData.stream().mapToDouble(HeartbeatStaticResDto::getAvgBpm).max().orElse(0);
        return new HeartbeatSearchResDto(new UserHeartbeatResDto(user), formatDate(date.atStartOfDay()), minBpm, maxBpm, staticData);
    }

    // 검색 타입 유효성 검사
    private SearchTypeEnum validateSearchType(String searchTypeEnum) {
        try {
            return SearchTypeEnum.valueOf(searchTypeEnum);
        } catch (IllegalArgumentException e) {
            throw new AppException(HeartbeatErrorCode.NOT_EXIST_SEARCH_TYPE);
        }
    }
}
