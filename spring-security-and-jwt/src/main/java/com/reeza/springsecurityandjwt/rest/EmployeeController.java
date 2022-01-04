package com.reeza.springsecurityandjwt.rest;

import java.net.URI;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

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
	
	@PostMapping("/role/addtouser")
	public ResponseEntity<?> addRoleToUser(@RequestBody EmployeeRole employeeRole){
		employeeService.addRoleToEmployee(employeeRole.getUsername(), employeeRole.getRolename()); // returns a void
		return ResponseEntity.ok().build();  //since there's no body just build the response entity
	}

}

@Data // we just need getters and setters
class EmployeeRole{
	private String username;
	private String rolename;
	
}
