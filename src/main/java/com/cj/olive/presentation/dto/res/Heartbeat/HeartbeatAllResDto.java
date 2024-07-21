package com.cj.olive.presentation.dto.res.Heartbeat;

import lombok.Builder;
import lombok.Getter;

import java.util.List;


@Builder
@Getter
public class HeartbeatAllResDto {
    private String date;
    private List<HeartbeatAllItemResDto> heartbeatList;

    public HeartbeatAllResDto(String date, List<HeartbeatAllItemResDto> heartbeatList) {
        this.date = date;
        this.heartbeatList = heartbeatList;
    }
}