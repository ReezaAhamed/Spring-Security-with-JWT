package com.reeza.springsecurityandjwt.rest;

import static java.util.Arrays.stream;

import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.fasterxml.jackson.core.exc.StreamWriteException;
import com.fasterxml.jackson.databind.DatabindException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.reeza.springsecurityandjwt.entity.Employee;
import com.reeza.springsecurityandjwt.entity.Role;
import com.reeza.springsecurityandjwt.service.EmployeeService;

import lombok.Data;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class EmployeeController {
	
	private final EmployeeService employeeService;
	private final Algorithm algorithm;
	
	@GetMapping("/employees")
	public ResponseEntity<List<Employee>> getEmployees(){
		return ResponseEntity.ok().body(employeeService.getEmployees());  //ok : 200 status code
	}
	
	@PostMapping("/employee")
	public ResponseEntity<Employee> saveEmployee(@RequestBody Employee employee){
		URI uri = URI.create(ServletUriComponentsBuilder.fromCurrentContextPath().path("/api/employee").toUriString()); //generate status code in this case 201
		return ResponseEntity.created(uri).body(employeeService.saveEmployee(employee));  
	}
	
	@PostMapping("/role")
	public ResponseEntity<Role> saveRole(@RequestBody Role role){
		URI uri = URI.create(ServletUriComponentsBuilder.fromCurrentContextPath().path("/api/role").toUriString()); 
		return ResponseEntity.ok().body(employeeService.saveRole(role));  //ok : 200 status code
	}
	
	@PostMapping("/role/addtoemployee")
	public ResponseEntity<?> addRoleToUser(@RequestBody EmployeeRole employeeRole){
		employeeService.addRoleToEmployee(employeeRole.getUsername(), employeeRole.getRolename()); // returns a void
		return ResponseEntity.ok().build();  //since there's no body just build the response entity
	}
	
	@GetMapping("/token/refresh")
	public void refreshToken(HttpServletRequest request, HttpServletResponse response) throws IOException{
		String authorizationHeader = request.getHeader(HttpHeaders.AUTHORIZATION); // key for the token
		if(authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) { // we are gonna send the "Bearer " along with the token we get from authentication 
			try {
				String refreshToken = authorizationHeader.substring("Bearer ".length()); // remove "Bearer ", because we only need the token
				//Algorithm algorithm = algorithForToken.getAlgorithm();
				JWTVerifier verifier = JWT.require(algorithm).build(); // create the verifier
				DecodedJWT decodedToken = verifier.verify(refreshToken); //verifying the token
				String username = decodedToken.getSubject();  //username that comes with the token // we don't need the password as he's already authenticated
				Employee employee = employeeService.getEmployee(username); // find the user
			
				String accessToken = JWT.create()
						.withSubject(employee.getUsername()) // somethig unique about the user should come in subject
						.withExpiresAt(new Date(System.currentTimeMillis() + 10*60*1000)) // 10 minutes validity 
						.withIssuer(request.getRequestURL().toString())
						.withClaim("roles", employee.getRoles().stream().map(Role::getName).collect(Collectors.toList())) 
						.sign(algorithm); // sign the token with the algorithm
				
				Map<String, String> tokens = new HashMap<>();
				tokens.put("Access Token", accessToken);
				tokens.put("Refresh Token", refreshToken);
				response.setContentType(MediaType.APPLICATION_JSON_VALUE); // I want it to be JSON
				new ObjectMapper().writeValue(response.getOutputStream(), tokens);
				
			} catch (Exception exception) {
				// log.error("Error logging inb: {}", exception.getMessage());
				response.setHeader("Error", exception.getMessage());  // setting some header deatils
				response.setStatus(HttpServletResponse.SC_FORBIDDEN); // 403, you don't have access
				//response.sendError(HttpServletResponse.SC_FORBIDDEN); // 403, you don't have access
				// instead of sending plain error, send in body
				Map<String, String> error = new HashMap<>();
				error.put("Error Message", exception.getMessage());
				response.setContentType(MediaType.APPLICATION_JSON_VALUE); // I want it to be JSON
				new ObjectMapper().writeValue(response.getOutputStream(), error); // throws IOException, make sure to add it
			} 
	} else {
		throw new RuntimeException("Refresh token is missing!");
		}
	}

}

@Data // we just need getters and setters
class EmployeeRole{
	private String username;
	private String rolename;
	
}
