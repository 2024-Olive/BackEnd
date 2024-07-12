package com.cj.olive.presentation.controller.User;

import com.cj.olive.domain.User.service.UserService;
import com.cj.olive.global.common.DataResponseDto;
import com.cj.olive.global.common.ResponseDto;
import com.cj.olive.presentation.dto.req.User.UserReqDto;
import com.cj.olive.presentation.dto.res.User.UserResDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/user")
public class UserController {

    private final UserService userService;

    @PostMapping("sign-up")
    public ResponseEntity<ResponseDto> signUp(@RequestBody @Valid UserReqDto userReqDto) {
        UserResDto userResDto = userService.signUp(userReqDto);

        return ResponseEntity.status(201).body(DataResponseDto.of(userResDto, 201));
    }

    @PatchMapping("change-role")
    public ResponseEntity<ResponseDto> changeRole(@RequestParam(name = "userId") String userName, @RequestParam(name = "role") String role) {

        String roleChanged = userService.changeRole(userName, role);

        return ResponseEntity.status(201).body(DataResponseDto.of(roleChanged, 200));
    }

}
