package com.example.demo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.client.RestTemplate;

@Configuration
public class AppConfig {

	@Bean
	// springBoot3開始bean開頭public可以不用寫
	PasswordEncoder passwordEncoder() {// 名字一定要是passwordEncoder
		return new BCryptPasswordEncoder();
	}
	
    @Bean
    RestTemplate restTemplate() {
        return new RestTemplate();
    }
}
