package com.cj.olive.presentation.dto.res.Report;

import com.cj.olive.domain.Report.entity.Report;
import com.cj.olive.presentation.dto.res.User.UserResDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import static com.cj.olive.global.util.DateUtil.formatDate;
import static com.cj.olive.global.util.DateUtil.formatHour;

@Builder
@AllArgsConstructor
@Getter
public class ReportResDto {
    private Long report_id;
    private Float latitude;
    private Float longitude;
    private String regDate;
    private String regHour;
    private UserResDto userResDto;

    public ReportResDto(Report report, UserResDto userResDto) {
        this.report_id = report.getId();
        this.latitude = report.getLatitude();
        this.longitude = report.getLongitude();
        this.regDate = formatDate(report.getRegTime());
        this.regHour = formatHour(report.getRegTime());
        this.userResDto = userResDto;
    }
}
