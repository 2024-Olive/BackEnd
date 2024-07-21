package com.cj.olive.domain.User.service;

import com.cj.olive.domain.User.entity.User;
import com.cj.olive.domain.User.error.UserErrorCode;
import com.cj.olive.domain.User.model.UserTypeEnum;
import com.cj.olive.domain.User.repository.UserRepository;
import com.cj.olive.global.error.GlobalErrorCode;
import com.cj.olive.global.error.exception.AppException;
import com.cj.olive.presentation.dto.req.User.UserReqDto;
import com.cj.olive.presentation.dto.res.User.UserResDto;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    @Transactional
    public UserResDto signUp(UserReqDto userReqDto) {
        // 아이디 중복 검사
        if (userRepository.existsByUsername(userReqDto.getUsername())) {
            throw new AppException(UserErrorCode.ID_ALREADY_EXIST);
        }

        // TODO: 디바이스 토큰 유효성 검사
        // fcmUtil.validateToken(deviceToken);

        // 회원 저장
        User user = userRepository.save(new User(
                userReqDto.getUsername(),
                userReqDto.getNickname(),
                bCryptPasswordEncoder.encode(userReqDto.getPassword()),
                userReqDto.getPhoneNumber(),
                UserTypeEnum.USER
        ));

        // 첫번째 가입자면 관리자 지정
        if (user.getId() == 1) {
            user.updateUserType(UserTypeEnum.ADMIN);
        }

        return new UserResDto(user);
    }

    @Transactional
    public UserTypeEnum changeRole(String username, String role) {

        // 요청한 사용자 id
        User requestUser = getRequestUser();

        // 권한 체크
        if (!requestUser.isAdmin()) {
            throw new AppException(GlobalErrorCode.ONLY_ADMIN_ACCESS);
        }

        // 변경하고자 하는 회원
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new AppException(GlobalErrorCode.USER_NOT_FOUND));

        if (Objects.equals(role, UserTypeEnum.ADMIN.name())) {
            user.updateUserType(UserTypeEnum.ADMIN);
        } else if (Objects.equals(role, UserTypeEnum.USER.name())) {
            user.updateUserType(UserTypeEnum.USER);
        } else {
            throw new AppException(UserErrorCode.NONE_USER_TYPE);
        }

        return user.getUserType();
    }

    public int updateThreshold(String username, int threshold) {
        // 요청한 사용자 id
        User requestUser = getRequestUser();

        // 권한 검사
        if (!requestUser.isAdmin()) {
            throw new AppException(GlobalErrorCode.ONLY_ADMIN_ACCESS);
        }

        // 변경하고자 하는 회원
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new AppException(GlobalErrorCode.USER_NOT_FOUND));

        user.updateThresholdValue(threshold);

        return user.getThreshold();
    }


    // 사용자 가져오기
    public User getRequestUser() {
        String requestUserName = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByUsername(requestUserName)
                .orElseThrow(() -> new AppException(UserErrorCode.INVALID_ID_TOKEN));
    }
}
