package com.cj.olive.presentation.dto.req.User;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Builder
@AllArgsConstructor
@Getter
public class UserReqDto {
    @NotBlank(message = "아이디는 필수 입력 값입니다.")
    @Size(min = 4, max = 12, message = "아이디는 4~12자로 입력해 주세요.")
    @Pattern(regexp = "^[a-zA-Z0-9]*$", message = "아이디는 영어와 숫자를 사용해 주세요.")
    private String username;

    @NotBlank(message = "비밀번호는 필수 입력 값입니다.")
    @Size(min = 8, message = "비밀번호는 8자 이상 입력해 주세요.")
    @Pattern(regexp = "^(?=.*[a-zA-Z])(?=.*\\d)(?=.*[!@#$%^])[A-Za-z\\d!@#$%^]+$", message = "비밀번호는 영어, 숫자, 특수문자를 포함해 주세요.")
    private String password;

    @NotBlank(message = "이름은 필수 입력 값입니다.")
    private String nickname;

    @NotBlank(message = "전화번호는 필수 입력 값입니다.")
    @Size(min = 11, max = 11, message = "전화번호는 숫자로 11자만 입력이 가능합니다.")
    private String phoneNumber;

    @NotBlank(message = "디바이스 토큰은 필수 입력 값입니다.")
    private String deviceToken;
}
