package com.cj.olive.global.common.filter;

import com.cj.olive.domain.User.model.CustomUserDetails;
import com.cj.olive.global.common.DataResponseDto;
import com.cj.olive.global.common.ResponseDto;
import com.cj.olive.global.util.JwtUtil;
import com.cj.olive.presentation.dto.req.User.LoginReqDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;

@RequiredArgsConstructor
public class JwtAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    private final AuthenticationManager authenticationManager;

    private final JwtUtil jwtUtil;

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) {

        // 요청의 본문을 읽어서 UserReqDto 객체로 변환
        LoginReqDto authRequest;
        try {
            authRequest = new ObjectMapper().readValue(request.getInputStream(), LoginReqDto.class);

            // UserReqDto 객체에서 username과 password를 추출
            String username = authRequest.getUsername();
            String password = authRequest.getPassword();

            System.out.println(username);
            System.out.println(password);

            if (username == null || password == null) {
                throw new AuthenticationServiceException("Username or Password not provided");
            }

            //스프링 시큐리티에서 username과 password를 검증하기 위해서는 token에 담아야 함
            UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(username, password, null);

            //token에 담은 검증을 위한 AuthenticationManager로 전달
            return authenticationManager.authenticate(authToken);

        } catch (UsernameNotFoundException e) {
            unsuccessfulAuthentication(request, response, new UsernameNotFoundException(e.getMessage(), e));
            return null;
        } catch (AuthenticationException e) {
            unsuccessfulAuthentication(request, response, e);
            return null;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    //로그인 성공시 실행하는 메소드 (여기서 JWT를 발급하면 됨)
    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authentication) {

        CustomUserDetails customUserDetails = (CustomUserDetails) authentication.getPrincipal();

        String username = customUserDetails.getUsername();

        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        Iterator<? extends GrantedAuthority> iterator = authorities.iterator();
        GrantedAuthority auth = iterator.next();

        String role = auth.getAuthority();

        String accessToken = jwtUtil.createJwt(username, role, 60 * 60 * 10L);

        // 응답 헤더에 추가
        response.addHeader("Authorization", "Bearer " + accessToken);
        Map<String, String> tokens = Map.of("accessToken", accessToken);
        DataResponseDto<Map<String, String>> responseDto = DataResponseDto.of(tokens, 201, "로그인 되었습니다.");
        jwtUtil.writeResponse(response, responseDto, HttpServletResponse.SC_CREATED);
    }

    //로그인 실패시 실행하는 메소드
    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, AuthenticationException failed) {
        String errorMessage;
        int statusCode = HttpServletResponse.SC_UNAUTHORIZED;

        if (failed instanceof AuthenticationServiceException) {
            errorMessage = "아이디 혹은 비밀번호가 오지 않았습니다.";
            statusCode = HttpServletResponse.SC_BAD_REQUEST;
        } else if (failed instanceof UsernameNotFoundException) {
            errorMessage = "가입되지 않은 아이디입니다.";
        } else if (failed instanceof BadCredentialsException) {
            errorMessage = "비밀번호가 잘못되었습니다.";
        } else {
            errorMessage = "로그인에 실패하였습니다. " + failed.getMessage();
        }

        ResponseDto responseDto = ResponseDto.of(statusCode, errorMessage);
        jwtUtil.writeResponse(response, responseDto, statusCode);
    }


}
