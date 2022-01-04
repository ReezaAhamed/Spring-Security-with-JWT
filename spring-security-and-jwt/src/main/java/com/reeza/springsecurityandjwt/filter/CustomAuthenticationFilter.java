package com.reeza.springsecurityandjwt.filter;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class CustomAuthenticationFilter extends UsernamePasswordAuthenticationFilter {
	
	
	private final AuthenticationManager authenticationManager;

	@Override
	public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
			throws AuthenticationException { // when a user tries to log in...
		
		String username = request.getParameter("username");
		String password = request.getParameter("password");
		// log.info("Username is : {}", username);
		// log.info(Password is : {}", password);
		UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(username, password);
		
		return authenticationManager.authenticate(authenticationToken);
	}

	@Override
	protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain,
			Authentication authResult) throws IOException, ServletException { // user successfully logged in... as in attemptAuthentrication method is successful
		
		User user = (User)authResult.getPrincipal(); //logged in user
		Algorithm algorithm = Algorithm.HMAC256("secret".getBytes()); // algorithm from auth0 library. we added the auth0 dependency to pom.xml
		
		String accessToken = JWT.create()
				.withSubject(user.getUsername()) // somethig unique about the user should come in subject
				.withExpiresAt(new Date(System.currentTimeMillis() + 10*60*1000)) // 10 minutes validity 
				.withIssuer(request.getRequestURL().toString())
				.withClaim("roles", user.getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(Collectors.toList())) 
				.sign(algorithm); // sign the token with the algorithm
	
		String refreshToken = JWT.create()
				.withSubject(user.getUsername()) // somethig unique about the user should come in subject
				.withExpiresAt(new Date(System.currentTimeMillis() + 30*60*1000)) // 30 minutes validity
				.withIssuer(request.getRequestURL().toString())
				.sign(algorithm);
		
		response.setHeader("Access Token", accessToken);
		response.setHeader("Refresh Token", refreshToken);
		
		// besides sending the tokens on headers, I just need to send them as JSON in the body as well.. (Ofc, we need object mapper to send as JSON)
		Map<String, String> tokens = new HashMap<>();
		tokens.put("Access Token", accessToken);
		tokens.put("Refresh Token", refreshToken);
		response.setContentType(MediaType.APPLICATION_JSON_VALUE); // I want it to be JSON
		new ObjectMapper().writeValue(response.getOutputStream(), tokens);
		
	}
	

}
