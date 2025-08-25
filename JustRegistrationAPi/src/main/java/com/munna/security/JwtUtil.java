package com.munna.security;

import java.awt.RenderingHints.Key;
import java.util.Date;

import javax.crypto.SecretKey;

import org.springframework.stereotype.Component;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

@Component
public class JwtUtil {
	
	private final String SECRET="mysupersecretkeymysupersecretkey";//we have to use application.properties
	private final long EXPIRATION=1000 * 60 * 60;//1hour 1000 * 60 * 150 -2h 30 min
	
	private SecretKey getSigningKey()
	{
		return Keys.hmacShaKeyFor(SECRET.getBytes());
	}
	
	public String generateToken(String email)
	{
		return Jwts.builder()
				   .setSubject(email)
				   .setIssuedAt(new Date())
				   .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION))
				   .signWith(getSigningKey(), SignatureAlgorithm.HS256)
				   .compact();
	}

	public String extractUsername(String token)
	{
		return Jwts.parserBuilder()
				   .setSigningKey(getSigningKey())
				   .build()
				   .parseClaimsJws(token)
				   .getBody()
				   .getSubject();
	}
	
	private boolean isTokenExpaired(String token)
	{
		Date expiration  = Jwts.parserBuilder()
				               .setSigningKey(getSigningKey())
				               .build()
				               .parseClaimsJws(token)
							   .getBody()
							   .getExpiration();
		
		return expiration.before(new Date());
							   
		
	}
	
	public boolean  validateToken(String token,String email)
	{
		String username= extractUsername(token);
		return(username.equals(email) && !isTokenExpaired(token));
	}
	
	
}
