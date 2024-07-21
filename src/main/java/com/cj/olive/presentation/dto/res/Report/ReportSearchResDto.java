package com.cj.olive.presentation.dto.res.Report;


import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class ReportSearchResDto {
    private String regDate;
    private List<ReportSearchItemResDto> items;
}