package com.reeza.springsecurityandjwt.filter;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.auth0.jwt.algorithms.Algorithm;

@Configuration
public class AlgorithmForToken {
	
	@Bean
	public Algorithm getAlgorithm() {
		return Algorithm.HMAC256("secret".getBytes());
	}

}
