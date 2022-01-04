package com.reeza.springsecurityandjwt.filter;

import java.io.IOException;
import java.util.Date;
import java.util.stream.Collectors;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class CustomAuthenticationFilter extends UsernamePasswordAuthenticationFilter {
	
	
	private final AuthenticationManager authenticationManager;

	@Override
	public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
			throws AuthenticationException {
		
		String username = request.getParameter("username");
		String password = request.getParameter("password");
		// log.info("Username is : {}", username);
		// log.info(Password is : {}", password);
		UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(username, password);
		
		return authenticationManager.authenticate(authenticationToken);
	}

	@Override
	protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain,
			Authentication authResult) throws IOException, ServletException {
		
		User user = (User)authResult.getPrincipal(); //logged in user
		Algorithm algorithm = Algorithm.HMAC256("secret".getBytes());
		
		String accessToken = JWT.create()
				.withSubject(user.getUsername()) // somethig unique to the user should come in subject
				.withExpiresAt(new Date(System.currentTimeMillis() + 10*60*1000)) 
				.withIssuer(request.getRequestURL().toString())
				.withClaim("roles", user.getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(Collectors.toList()))
				.sign(algorithm);
	
		String refreshToken = JWT.create()
				.withSubject(user.getUsername()) // somethig unique to the user should come in subject
				.withExpiresAt(new Date(System.currentTimeMillis() + 30*60*1000))
				.withIssuer(request.getRequestURL().toString())
				.sign(algorithm);
		
		response.setHeader("Access Token", accessToken);
		response.setHeader("Refresh Token", refreshToken);
	}
	

}
