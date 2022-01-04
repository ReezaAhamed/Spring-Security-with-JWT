package com.reeza.springsecurityandjwt.security.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
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
	
	private final UserDetailsService userDetailsService;  // we need to create the bean, so it's available to use, we do that in EmployeeServiceImpl by implementing UserDetailsService(& of course    we need to override a the methiod loadUserbyUsername)
	private final BCryptPasswordEncoder bCryptPasswordEncoder; //we need to create a bean, which we will do in Password encoder bean creator class
	
	@Override
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
		// we can do in memory authentication or jdbc authetication etc.. but we wanna do using userDetailsService, which is a bean we are going to define in EmployeeServiceImpl, check it out.
		auth.userDetailsService(userDetailsService).passwordEncoder(bCryptPasswordEncoder);
	}

	@Override
	protected void configure(HttpSecurity http) throws Exception { //spring by defaullt uses the security system based on session which is stateful, tracking the suer by giving a cookie, we don't want that. we need jason web token system

		CustomAuthenticationFilter customAuthenticationFiler = new CustomAuthenticationFilter(authenticationManagerBean());
		customAuthenticationFiler.setFilterProcessesUrl("/api/login");
		
		http.csrf().disable();
		http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS); // we don't need the default system
		// http.authorizeRequests().anyRequest().permitAll(); // we allow anyone to use this application at the moment
		//http.authorizeRequests().antMatchers("api/login/**").permitAll(); // spring takes care of this enpoint /login, we don't need to say permit all. but if you want to change /login to say /api/login, what is done in first two lines.......//order matters
		http.authorizeRequests().antMatchers(HttpMethod.GET, "/api/employees/**").hasAuthority("ROLE_USER"); //to get the list of employees (or whatever comes after that enpoint ** in the context of get), one must have the role user
		http.authorizeRequests().antMatchers(HttpMethod.POST, "/api/employee/**").hasAuthority("ROLE_ADMIN"); //to save an employee (or what comes after that endpoint ** in the context of posrt), one must have the role admin
		http.authorizeRequests().antMatchers(HttpMethod.POST, "/api/role/**").hasAuthority("ROLE_ADMIN"); //to save a role or add role to user (**), one must have the role admin
		
		// now let's add the filters
		//http.addFilter(new CustomAuthenticationFilter(authenticationManagerBean())); // create the filter in new pacage filter
		http.addFilter(customAuthenticationFiler);
	}
	
	@Bean
	@Override
	public AuthenticationManager authenticationManagerBean() throws Exception {
		return super.authenticationManagerBean();
	}
	
	

}
