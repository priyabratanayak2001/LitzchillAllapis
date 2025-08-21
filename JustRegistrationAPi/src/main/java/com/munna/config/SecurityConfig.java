package com.munna.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.munna.security.JwtFilter;

@Configuration
public class SecurityConfig {
	
	 private final JwtFilter jwtFilter;

	    public SecurityConfig(JwtFilter jwtFilter) {
	        this.jwtFilter = jwtFilter;
	    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf().disable()  // disable CSRF for API clients like Postman
            .authorizeHttpRequests()
            .requestMatchers("/api/registration","/api/login").permitAll()   // allow anyone to register
            .anyRequest().authenticated()              // all other endpoints need login
            .and()
            .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }
}
