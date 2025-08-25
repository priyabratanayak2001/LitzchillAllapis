package com.munna.controller;

import java.util.HashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.munna.DTO.LoginRequest;
import com.munna.DTO.RegisterRequest;
import com.munna.entity.User;
import com.munna.repo.userRepo;
import com.munna.security.AuthResponse;
import com.munna.security.JwtUtil;

@RestController
@RequestMapping("/api")
public class restController {
	
	@Autowired
	private userRepo repo;
	
	@Autowired
    private JwtUtil jwtUtil;
	
	private PasswordEncoder passwordencoder = new BCryptPasswordEncoder();
	
	@PostMapping("/registration")
	public ResponseEntity<String>  register(@RequestBody RegisterRequest request)
	{
		   if  (repo.findByEmail(request.getEmail()).isPresent())
		   {
			   return  ResponseEntity.ok("email already exist");
		   }
		   
		   User user = new User();
		   user.setName(request.getName());
		   user.setEmail(request.getEmail());
		   user.setPassword(passwordencoder.encode(request.getPassword()));
		     
		   repo.save(user);
		   return  ResponseEntity.ok("User registered successsfully");
		
	}
	
	@PostMapping("/login")
	public ResponseEntity<?> login(@RequestBody LoginRequest req) {
	    User user = repo.findByEmail(req.getEmail()).orElse(null);

	    if (user == null) {
	        return ResponseEntity.status(401).body("Invalid email or password");
	    }
	    
	    String token = jwtUtil.generateToken(user.getEmail());
	    
	    return ResponseEntity.ok(new AuthResponse(token));
	}


}
