package com.example.ProjectManagementBackend.config;



import com.example.ProjectManagementBackend.util.JwtUtil;

import org.springframework.messaging.*;
import org.springframework.messaging.simp.stomp.*;
import org.springframework.messaging.support.*;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class WebSocketAuthInterceptor implements ChannelInterceptor {

    private final JwtUtil jwtUtil;

    public WebSocketAuthInterceptor(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {

        StompHeaderAccessor accessor =
                MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);

        if (accessor == null) return message;

        if (StompCommand.CONNECT.equals(accessor.getCommand())) {

            String authHeader = accessor.getFirstNativeHeader("Authorization");

            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                return message; // ⚠️ do NOT crash handshake
            }

            try {
                String token = authHeader.substring(7);
                UUID userId = jwtUtil.extractUserId(token);

                accessor.setUser(() -> userId.toString());

            } catch (Exception e) {
                // ⚠️ DO NOT throw RuntimeException here
                System.out.println("Invalid WebSocket JWT: " + e.getMessage());
            }
        }

        return message;
    }
}