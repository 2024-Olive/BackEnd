package com.cj.olive.presentation.dto.res.Report;


import com.cj.olive.domain.Report.entity.Report;
import lombok.AllArgsConstructor;
import lombok.Getter;

import static com.cj.olive.global.util.DateUtil.formatHour;


@Getter
@AllArgsConstructor
public class ReportSearchItemResDto {
    private String regHour;
    private String username;

    public ReportSearchItemResDto(Report report) {
        this.regHour = formatHour(report.getRegTime());
        this.username = report.getUser().getUsername();
    }
}