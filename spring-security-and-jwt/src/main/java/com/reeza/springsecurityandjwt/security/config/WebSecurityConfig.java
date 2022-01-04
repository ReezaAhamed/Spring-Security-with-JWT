package com.reeza.springsecurityandjwt.security.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import com.reeza.springsecurityandjwt.filter.CustomAuthenticationFilter;

import lombok.RequiredArgsConstructor;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {
	
	private final UserDetailsService userDetailsService;  // we need to create the bean, so it's available to use, we do that in EmployeeServiceImpl
	private final BCryptPasswordEncoder bCryptPasswordEncoder; //we need to create a bean, which we will do in Password encoder class
	
	@Override
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
		// we can do in memory authentication or jdbc authetication etc.. but we wanna do using userDetailsService, which is a bean we are going to define in EmployeeServiceImpl, check it out.
		auth.userDetailsService(userDetailsService).passwordEncoder(bCryptPasswordEncoder);
	}

	@Override
	protected void configure(HttpSecurity http) throws Exception { //spring by defaullt uses the security system based on session, something like cookies, we don't want that. we need jason web token system

		http.csrf().disable();
		http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS); // we don;t need the default system
		http.authorizeRequests().anyRequest().permitAll(); // we allow anyone to use this application at the moment
		// now let's add the filters
		http.addFilter(new CustomAuthenticationFilter(authenticationManagerBean())); // create the filter in new pacage filter
	}
	
	@Bean
	@Override
	public AuthenticationManager authenticationManagerBean() throws Exception {
		return super.authenticationManagerBean();
	}
	
	

}
