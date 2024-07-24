package com.cj.olive.presentation.controller.Report;

import com.cj.olive.domain.Report.service.ReportService;
import com.cj.olive.global.common.DataResponseDto;
import com.cj.olive.global.common.ResponseDto;
import com.cj.olive.presentation.dto.req.Report.ReportReqDto;
import com.cj.olive.presentation.dto.res.Report.ReportResDto;
import com.cj.olive.presentation.dto.res.Report.ReportSearchResDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/report")
public class ReportController {

    private final ReportService reportService;

    // 신고 등록
    @PostMapping
    public ResponseEntity<ResponseDto> reportReg(@RequestBody @Valid ReportReqDto reportReqDto) {
        ReportResDto reportResDto = reportService.reportReg(reportReqDto);

        return ResponseEntity.status(201).body(DataResponseDto.of(reportResDto, 201));
    }

    // 권한 별 신고 조회
    @GetMapping
    public ResponseEntity<ResponseDto> getReports() {
        List<ReportSearchResDto> reports = reportService.getReports();
        return ResponseEntity.status(200).body(DataResponseDto.of(reports, 200));
    }

    // 관리자가 사용자 별로 조회
    @GetMapping("/{username}")
    public ResponseEntity<ResponseDto> getReportsByUsername(@PathVariable String username) {
        ReportSearchResDto report = reportService.getReportsByUsername(username);
        return ResponseEntity.status(200).body(DataResponseDto.of(report, 200));
    }


}
