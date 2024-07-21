package com.cj.olive.presentation.controller.Heartbeat;

import com.cj.olive.domain.Heartbeat.service.HeartbeatService;
import com.cj.olive.global.common.DataResponseDto;
import com.cj.olive.global.common.ResponseDto;
import com.cj.olive.presentation.dto.res.Heartbeat.HeartbeatAllResDto;
import com.cj.olive.presentation.dto.res.Heartbeat.HeartbeatSearchResDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/heartbeat")
public class HeartbeatController {

    private final HeartbeatService heartbeatService;

    // 사용자의 자신 심박수 검색
    @GetMapping("/{date}")
    public ResponseEntity<ResponseDto> getUserHeartbeatStats(
            @PathVariable(name = "date") LocalDate date,
            @RequestParam(name = "searchTypeEnum") String searchTypeEnum
    ) {
        HeartbeatSearchResDto heartbeatResDto = heartbeatService.getUserHeartbeatStats(searchTypeEnum, date);

        // TODO 응답 코드 다시 고민해보기
        if (heartbeatResDto == null) {
            return ResponseEntity.status(204).body(DataResponseDto.of(null, 204, "기록되어 있는 심박수가 없습니다."));
        }

        return ResponseEntity.status(200).body(DataResponseDto.of(heartbeatResDto, 200));
    }

    // 관리자의 사용자 심박수 검색
    @GetMapping("/{username}/{date}")
    public ResponseEntity<ResponseDto> getAdminHeartbeatStats(
            @PathVariable(name = "date") LocalDate date,
            @PathVariable(name = "username") String username,
            @RequestParam(name = "searchTypeEnum") String searchTypeEnum
    ) {
        HeartbeatSearchResDto heartbeatSearchResDto = heartbeatService.getAdminHeartbeatStats(username, searchTypeEnum, date);

        // TODO 응답 코드 다시 고민해보기
        if (heartbeatSearchResDto == null) {
            return ResponseEntity.status(204).body(DataResponseDto.of(null, 204, "기록되어 있는 심박수가 없습니다."));
        }

        return ResponseEntity.status(200).body(DataResponseDto.of(heartbeatSearchResDto, 200));
    }

    // 관리자의 전체 사용자 심박수 조회
    @GetMapping
    public ResponseEntity<ResponseDto> getUsersDailyHeartbeatStats() {
        List<HeartbeatAllResDto> heartbeatAllResDtoList = heartbeatService.getDailyHeartbeatStats();

        // TODO 응답 코드 다시 고민해보기
        if (heartbeatAllResDtoList == null) {
            return ResponseEntity.status(204).body(DataResponseDto.of(null, 204, "기록되어 있는 심박수가 없습니다."));
        }

        return ResponseEntity.status(200).body(DataResponseDto.of(heartbeatAllResDtoList, 200));
    }
}
