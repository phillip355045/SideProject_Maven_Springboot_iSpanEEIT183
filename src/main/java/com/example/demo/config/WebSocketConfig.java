package com.example.demo.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
@EnableWebSocket
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

	@Override
	public void configureMessageBroker(MessageBrokerRegistry config) {
		config.enableSimpleBroker("/topic", "/queue"); //增加"/queue"
		config.setApplicationDestinationPrefixes("/app");
		config.setUserDestinationPrefix("/user"); //增加
	}

	 @Override
	    public void registerStompEndpoints(StompEndpointRegistry registry) {
	        // 確保這裡的端點是 /ws
	        registry.addEndpoint("/ws").withSockJS();
	    }
}