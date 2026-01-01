package com.example.netapp.config;

import com.example.netapp.entity.UserEntity;
import com.example.netapp.repository.UserRepository;
import com.example.netapp.services.JwtService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

/**
 * Interceptor for WebSocket STOMP messages that handles JWT authentication.
 * Authenticates users during CONNECT/STOMP commands and sets the security
 * context.
 */
@Component
public class WebSocketAuthInterceptor implements ChannelInterceptor {

    private static final Logger logger = LoggerFactory.getLogger(WebSocketAuthInterceptor.class);
    private static final String BEARER_PREFIX = "Bearer ";

    private final JwtService jwtService;
    private final UserRepository userRepository;

    public WebSocketAuthInterceptor(JwtService jwtService, UserRepository userRepository) {
        this.jwtService = jwtService;
        this.userRepository = userRepository;
    }

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);

        if (accessor != null && isConnectCommand(accessor.getCommand())) {
            authenticateConnection(accessor);
        }

        return message;
    }

    private boolean isConnectCommand(StompCommand command) {
        return StompCommand.CONNECT.equals(command) || StompCommand.STOMP.equals(command);
    }

    private void authenticateConnection(StompHeaderAccessor accessor) {
        String authHeader = extractAuthorizationHeader(accessor);

        if (authHeader == null || !authHeader.startsWith(BEARER_PREFIX)) {
            logger.warn("WebSocket connection attempt without valid Authorization header");
            return;
        }

        try {
            String token = authHeader.substring(BEARER_PREFIX.length());
            Long userId = jwtService.extractUserId(token);

            userRepository.findById(userId).ifPresentOrElse(
                    user -> setAuthentication(accessor, user),
                    () -> logger.warn("WebSocket authentication failed: User not found with id: {}", userId));
        } catch (Exception e) {
            logger.error("WebSocket authentication failed", e);
        }
    }

    private String extractAuthorizationHeader(StompHeaderAccessor accessor) {
        String header = accessor.getFirstNativeHeader("Authorization");
        if (header == null) {
            header = accessor.getFirstNativeHeader("authorization");
        }
        if (header == null) {
            header = accessor.getFirstNativeHeader("AUTHORIZATION");
        }
        return header;
    }

    private void setAuthentication(StompHeaderAccessor accessor, UserEntity user) {
        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                user,
                null,
                user.getAuthorities());

        accessor.setUser(authentication);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        logger.debug("WebSocket authenticated user: {} (userId: {})", user.getUsername(), user.getUserId());
    }
}
