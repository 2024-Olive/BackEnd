package com.cj.olive.domain.Report.service;

import com.cj.olive.domain.Report.entity.Report;
import com.cj.olive.domain.Report.repository.ReportRepository;
import com.cj.olive.domain.User.entity.User;
import com.cj.olive.domain.User.error.UserErrorCode;
import com.cj.olive.domain.User.repository.UserRepository;
import com.cj.olive.domain.User.service.UserService;
import com.cj.olive.global.error.exception.AppException;
import com.cj.olive.presentation.dto.req.Report.ReportReqDto;
import com.cj.olive.presentation.dto.res.Report.ReportResDto;
import com.cj.olive.presentation.dto.res.Report.ReportSearchItemResDto;
import com.cj.olive.presentation.dto.res.Report.ReportSearchResDto;
import com.cj.olive.presentation.dto.res.User.UserResDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReportService {
    private final ReportRepository reportRepository;
    private final UserRepository userRepository;
    private final UserService userService;

    @Transactional
    public ReportResDto reportReg(ReportReqDto reportReqDto) {

        // 요청한 사용자 아이디 가져오기
        String requestUserName = SecurityContextHolder.getContext().getAuthentication().getName();


        // 요청한 사용자
        User requestUser = userRepository.findByUsername(requestUserName)
                .orElseThrow(() -> new AppException(UserErrorCode.INVALID_ID_TOKEN));

        // Report 생성
        Report report = Report
                .builder()
                .latitude(reportReqDto.getLatitude())
                .longitude(reportReqDto.getLongitude())
                .bpm(reportReqDto.getBpm())
                .user(requestUser)
                .build();

        // Report 저장
        reportRepository.save(report);

        // TODO: push 알림 to 관리자

        // 반환
        return new ReportResDto(report, new UserResDto(requestUser));
    }

    public List<ReportSearchResDto> getReports() {
        User user = userService.getRequestUser();
        List<Report> reports;
        if (user.isAdmin()) {
            reports = reportRepository.findAll();
        } else {
            reports = reportRepository.findByUserId(user.getId());
        }
        return reports.stream()
                .collect(Collectors.groupingBy(
                        report -> report.getRegTime().toLocalDate(),
                        Collectors.mapping(ReportSearchItemResDto::new, Collectors.toList())
                ))
                .entrySet().stream()
                .map(entry -> new ReportSearchResDto(entry.getKey().toString(), entry.getValue()))
                .collect(Collectors.toList());
    }

    public ReportSearchResDto getReportsByUsername(String username) {
        List<Report> reports = reportRepository.findByUserUsername(username);
        List<ReportSearchItemResDto> items = reports.stream()
                .map(ReportSearchItemResDto::new)
                .collect(Collectors.toList());
        return new ReportSearchResDto(username, items);
    }
}
