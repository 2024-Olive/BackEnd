package com.cj.olive.presentation.controller.User;

import com.cj.olive.domain.User.model.UserTypeEnum;
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

    @PostMapping("/sign-up")
    public ResponseEntity<ResponseDto> signUp(@RequestBody @Valid UserReqDto userReqDto) {
        userService.signUp(userReqDto);

        return ResponseEntity.status(201).body(DataResponseDto.of(201));
    }

    @GetMapping
    public ResponseEntity<ResponseDto> getUserInfo() {
        UserResDto userResDto = userService.getUserInfo();

        return ResponseEntity.status(200).body(DataResponseDto.of(userResDto, 200));
    }

    @PatchMapping("/change-role")
    public ResponseEntity<ResponseDto> changeRole(@RequestParam(name = "username") String username, @RequestParam(name = "role") String role) {

        UserTypeEnum roleChanged = userService.changeRole(username, role);

        return ResponseEntity.status(201).body(DataResponseDto.of(roleChanged, 200));
    }

    @PatchMapping("/{username}")
    public ResponseEntity<ResponseDto> updateThreshold(@PathVariable(name = "username") String username, @RequestParam(name = "threshold") int threshold) {
        int thresholdUpdated = userService.updateThreshold(username, threshold);

        return ResponseEntity.status(201).body(DataResponseDto.of(thresholdUpdated, 200));
    }
}
