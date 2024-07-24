package com.cj.olive.presentation.dto.res.Heartbeat;

import com.cj.olive.presentation.dto.res.User.UserHeartbeatResDto;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Builder
@Getter
public class HeartbeatSearchResDto {
    private UserHeartbeatResDto user;
    private String date;
    private int minBpm;
    private int maxBpm;
    private List<HeartbeatStaticResDto> staticData;

    public HeartbeatSearchResDto(UserHeartbeatResDto user, String date, int minBpm, int maxBpm, List<HeartbeatStaticResDto> staticData) {
        this.user = user;
        this.date = date;
        this.staticData = staticData;
        this.minBpm = minBpm;
        this.maxBpm = maxBpm;
    }
}
