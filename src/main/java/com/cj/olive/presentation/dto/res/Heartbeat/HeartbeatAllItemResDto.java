package com.cj.olive.presentation.dto.res.Heartbeat;

import lombok.Builder;
import lombok.Getter;


@Builder
@Getter
public class HeartbeatAllItemResDto {
    private Integer minBpm;
    private Integer maxBpm;
    private String username;

    public HeartbeatAllItemResDto(Integer minBpm, Integer maxBpm, String username) {
        this.minBpm = minBpm;
        this.maxBpm = maxBpm;
        this.username = username;
    }
}