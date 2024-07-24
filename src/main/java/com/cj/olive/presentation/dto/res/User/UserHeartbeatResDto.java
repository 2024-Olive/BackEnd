package com.cj.olive.presentation.dto.res.User;

import com.cj.olive.domain.User.entity.User;
import com.cj.olive.domain.User.model.UserTypeEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Builder
@AllArgsConstructor
@Getter
public class UserHeartbeatResDto {
    private String username;
    private String nickname;
    private String phoneNumber;
    private UserTypeEnum userType;
    private int threshold;

    public UserHeartbeatResDto(User user) {
        this.threshold = user.getThreshold();
        this.username = user.getUsername();
        this.nickname = user.getNickname();
        this.phoneNumber = user.getPhoneNumber();
        this.userType = user.getUserType();
    }
}
