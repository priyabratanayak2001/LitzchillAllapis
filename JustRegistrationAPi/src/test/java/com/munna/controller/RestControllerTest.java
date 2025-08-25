package com.munna.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.munna.DTO.LoginRequest;
import com.munna.DTO.RegisterRequest;
import com.munna.config.SecurityConfig;
import com.munna.entity.User;
import com.munna.repo.userRepo;
import com.munna.security.JwtUtil;

@WebMvcTest(restController.class) // your controller class
@Import(SecurityConfig.class)
public class RestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private userRepo repo;   // mocked repository

    @MockBean
    private JwtUtil jwtUtil; // mocked jwt util

    @Autowired
    private ObjectMapper objectMapper;

    private RegisterRequest registerRequest;
    private LoginRequest loginRequest;
    private User user;

    @BeforeEach
    void setup() {
        registerRequest = new RegisterRequest();
        registerRequest.setName("Test User");
        registerRequest.setEmail("test@example.com");
        registerRequest.setPassword("password123");

        loginRequest = new LoginRequest();
        loginRequest.setEmail("test@example.com");
        loginRequest.setPassword("password123");

        user = new User();
        user.setName("Test User");
        user.setEmail("test@example.com");
        user.setPassword("encodedPassword"); // assume already encoded
    }

    @Test
    void testRegister_NewUser_Success() throws Exception {
        when(repo.findByEmail(registerRequest.getEmail())).thenReturn(Optional.empty());
        when(repo.save(any(User.class))).thenReturn(user);

        mockMvc.perform(post("/api/registration")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isOk())
                .andExpect(content().string("User registered successsfully"));
    }

    @Test
    void testRegister_EmailAlreadyExists() throws Exception {
        when(repo.findByEmail(registerRequest.getEmail())).thenReturn(Optional.of(user));

        mockMvc.perform(post("/api/registration")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isOk())
                .andExpect(content().string("email already exist"));
    }

    @Test
    void testLogin_Success() throws Exception {
        when(repo.findByEmail(loginRequest.getEmail())).thenReturn(Optional.of(user));
        when(jwtUtil.generateToken(user.getEmail())).thenReturn("mocked-jwt-token");

        mockMvc.perform(post("/api/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.jwt").value("mocked-jwt-token")); 
    }

    @Test
    void testLogin_InvalidEmail() throws Exception {
        when(repo.findByEmail(loginRequest.getEmail())).thenReturn(Optional.empty());

        mockMvc.perform(post("/api/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isUnauthorized())
                .andExpect(content().string("Invalid email or password"));
    }
}
