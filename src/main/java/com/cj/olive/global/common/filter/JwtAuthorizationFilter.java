package com.cj.olive.global.common.filter;

import com.cj.olive.domain.User.entity.User;
import com.cj.olive.domain.User.error.UserErrorCode;
import com.cj.olive.domain.User.model.CustomUserDetails;
import com.cj.olive.domain.User.model.UserTypeEnum;
import com.cj.olive.global.common.ResponseDto;
import com.cj.olive.global.error.GlobalErrorCode;
import com.cj.olive.global.util.JwtUtil;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@RequiredArgsConstructor
public class JwtAuthorizationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String authorization = request.getHeader("Authorization");

        String requestURI = request.getRequestURI();
        boolean isSignUpOrSignIn = requestURI.equals("/api/v1/user/sign-up") || requestURI.equals("/api/v1/user/sign-in");

        if (authorization == null || !authorization.startsWith("Bearer ")) {
            if (!isSignUpOrSignIn) {
                ResponseDto responseDto = ResponseDto.of(400, UserErrorCode.ACCESS_TOKEN_REQUIRED.getMessage());
                jwtUtil.writeResponse(response, responseDto, HttpServletResponse.SC_UNAUTHORIZED);
            } else {
                filterChain.doFilter(request, response);
            }
            return;
        }

        String token = authorization.split(" ")[1];

        try {
            if (jwtUtil.isExpired(token)) {
                ResponseDto responseDto = ResponseDto.of(401, GlobalErrorCode.EXPIRED_JWT.getMessage());
                jwtUtil.writeResponse(response, responseDto, HttpServletResponse.SC_UNAUTHORIZED);
                return;
            }

            String username = jwtUtil.getUsername(token);
            String roleString = jwtUtil.getRole(token);
            UserTypeEnum role = UserTypeEnum.USER;

            if (roleString.equals(UserTypeEnum.ADMIN.name())) {
                role = UserTypeEnum.ADMIN;
            }

            User user = User.builder()
                    .username(username)
                    .userType(role)
                    .build();

            CustomUserDetails customUserDetails = new CustomUserDetails(user);
            Authentication authToken = new UsernamePasswordAuthenticationToken(customUserDetails, null, customUserDetails.getAuthorities());
            SecurityContextHolder.getContext().setAuthentication(authToken);
        } catch (ExpiredJwtException e) {
            ResponseDto responseDto = ResponseDto.of(401, GlobalErrorCode.EXPIRED_JWT.getMessage());
            jwtUtil.writeResponse(response, responseDto, HttpServletResponse.SC_UNAUTHORIZED);
            return;
        } catch (Exception e) {
            System.out.println(e.getMessage());
            ResponseDto responseDto = ResponseDto.of(401, GlobalErrorCode.INVALID_TOKEN.getMessage());
            jwtUtil.writeResponse(response, responseDto, 401);
            return;
        }

        filterChain.doFilter(request, response);
    }
}

