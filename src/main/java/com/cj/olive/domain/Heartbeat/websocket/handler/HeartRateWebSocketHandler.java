package com.cj.olive.domain.Heartbeat.websocket.handler;

import com.cj.olive.domain.Heartbeat.service.HeartbeatService;
import com.cj.olive.domain.User.entity.User;
import com.cj.olive.domain.User.repository.UserRepository;
import com.cj.olive.global.error.GlobalErrorCode;
import com.cj.olive.global.error.exception.AppException;
import com.cj.olive.global.util.JwtUtil;
import io.jsonwebtoken.JwtException;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Getter
@Component
@RequiredArgsConstructor
@EnableScheduling
public class HeartRateWebSocketHandler extends TextWebSocketHandler {

    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;
    private final HeartbeatService heartbeatService;

    private final Map<Long, WebSocketSession> sessions = new ConcurrentHashMap<>();

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        String authHeader = session.getHandshakeHeaders().getFirst("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            try {
                String username = jwtUtil.extractUsername(token);
                User user = userRepository.findByUsername(username)
                        .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));
                sessions.put(user.getId(), session);
            } catch (JwtException | IllegalArgumentException e) {
                log.error("JWT 토큰 오류: ", e);
                session.close(CloseStatus.NOT_ACCEPTABLE.withReason("Unauthorized"));
            }
        } else {
            session.close(CloseStatus.NOT_ACCEPTABLE.withReason("Unauthorized"));
        }
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        String payload = message.getPayload();
        int heartRate = Integer.parseInt(payload);
        Long userId = getUserIdFromSession(session);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new AppException(GlobalErrorCode.USER_NOT_FOUND));

        // 심박수가 임계치를 초과할 경우 관리자에게 알림 전송
        heartbeatService.cacheHeartbeatPerSecond(heartRate, user);
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        sessions.entrySet().removeIf(entry -> entry.getValue().equals(session));
    }

    private Long getUserIdFromSession(WebSocketSession session) {
        return sessions.entrySet().stream()
                .filter(entry -> entry.getValue().equals(session))
                .map(Map.Entry::getKey)
                .findFirst()
                .orElseThrow(() -> new AppException(GlobalErrorCode.USER_NOT_FOUND));
    }
}
