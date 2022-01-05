package com.reeza.springsecurityandjwt.filter;

import static java.util.Arrays.stream;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class CustomAuthorizationFilter extends OncePerRequestFilter{
	
	private final Algorithm algorithm;

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException { // this is gonna intercept each and every request coming in

		//we don't need to intercept login path "/api/login"...so,
		if(request.getServletPath().equals("/api/login")) { //do nothing
			filterChain.doFilter(request, response); // continue the request
			
		} else {
			String authorizationHeader = request.getHeader(HttpHeaders.AUTHORIZATION); // key for the token
			if(authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) { // we are gonna send the "Bearer " along with the token we get from authentication 
				try {
					String token = authorizationHeader.substring("Bearer ".length()); // remove "Bearer ", because we only need the token
					//Algorithm algorithm = algorithForToken.getAlgorithm();
					JWTVerifier verifier = JWT.require(algorithm).build(); // create the verifier
					DecodedJWT decodedToken = verifier.verify(token); //verifying the token
					String username = decodedToken.getSubject();  //username that comes with the token // we don't need the password as he's already authenticated
					String[] roles = decodedToken.getClaim("roles").asArray(String.class); // "roles" is the key we gave for the ROLES for token in authentication // we also need to say how we want to collect the roles, so asArray and we know the roles are strings, so String.class
					Collection<SimpleGrantedAuthority> authorities = new ArrayList<>(); // we need to convert thr roles to something that extends GrantedAuthority
					stream(roles).forEach(role -> { // loopin through each role and convert each of them to SimpleGrantedAuthority // Remember, SimpleGrantedAuthority extends GrantedAuthority
						authorities.add(new SimpleGrantedAuthority(role));
					});
					UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(username, null, authorities);
					SecurityContextHolder.getContext().setAuthentication(authenticationToken); // this is how we tell spring, here's the user and the roles he has, so he can do thsese stuff in the application
					filterChain.doFilter(request, response); // we still need to the request to continue
				} catch (Exception exception) {
					// log.error("Error logging inb: {}", exception.getMessage());
					response.setHeader("Error", exception.getMessage());  // setting some header deatils
					response.setStatus(HttpServletResponse.SC_FORBIDDEN); // 403, you don't have access
					//response.sendError(HttpServletResponse.SC_FORBIDDEN); // 403, you don't have access
					// instead of sending plain error, send in body
					Map<String, String> error = new HashMap<>();
					error.put("Error Message", exception.getMessage());
					response.setContentType(MediaType.APPLICATION_JSON_VALUE); // I want it to be JSON
					new ObjectMapper().writeValue(response.getOutputStream(), error);
				}
			} else {
				filterChain.doFilter(request, response); // continue the request
			}
		}
		
	}

}
