package com.cj.olive.presentation.dto.res.Heartbeat;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.Date;

import static com.cj.olive.global.util.DateUtil.convertToLocalDateTime;
import static com.cj.olive.global.util.DateUtil.formatDate;

@Builder
@Getter
public class HeartbeatStaticResDto {
    private String dateTime;
    private Double avgBpm;
    private Integer minBpm;
    private Integer maxBpm;

    public HeartbeatStaticResDto(String dateTime, Double avgBpm, Integer minBpm, Integer maxBpm) {
        this.dateTime = dateTime;
        this.avgBpm = avgBpm;
        this.minBpm = minBpm;
        this.maxBpm = maxBpm;
    }

    public HeartbeatStaticResDto(Integer hour, Double avgBpm, Integer minBpm, Integer maxBpm) {
        this.dateTime = hour != null ? String.valueOf(hour) : null;
        this.avgBpm = avgBpm;
        this.minBpm = minBpm;
        this.maxBpm = maxBpm;
    }

    public HeartbeatStaticResDto(Date dayOfWeek, Double avgBpm, Integer minBpm, Integer maxBpm) {
        LocalDateTime date = convertToLocalDateTime(dayOfWeek);
        this.dateTime = formatDate(date);
        this.avgBpm = avgBpm;
        this.minBpm = minBpm;
        this.maxBpm = maxBpm;
    }

    public HeartbeatStaticResDto(Double week, Double avgBpm, Integer minBpm, Integer maxBpm) {
        this.dateTime = week != null ? String.valueOf(week) : null;
        this.avgBpm = avgBpm;
        this.minBpm = minBpm;
        this.maxBpm = maxBpm;
    }
}