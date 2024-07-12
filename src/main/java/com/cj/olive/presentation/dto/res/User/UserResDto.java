package com.cj.olive.presentation.dto.res.User;

import com.cj.olive.domain.User.entity.User;
import com.cj.olive.domain.User.model.UserTypeEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Builder
@AllArgsConstructor
@Getter
public class UserResDto {
    private String username;
    private String password;
    private String nickname;
    private String phoneNumber;
    private UserTypeEnum userType;

    public UserResDto(User user) {
        this.username = user.getUsername();
        this.password = user.getPassword();
        this.nickname = user.getNickname();
        this.phoneNumber = user.getPhoneNumber();
        this.userType = user.getUserType();
    }
}
