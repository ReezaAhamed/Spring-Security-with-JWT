package com.reeza.springsecurityandjwt;

import java.util.ArrayList;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import com.reeza.springsecurityandjwt.entity.Employee;
import com.reeza.springsecurityandjwt.entity.Role;
import com.reeza.springsecurityandjwt.service.EmployeeService;

@SpringBootApplication
public class SpringSecurityAndJwtApplication {

	public static void main(String[] args) {
		SpringApplication.run(SpringSecurityAndJwtApplication.class, args);
	}
	
	@Bean // so just it's ready to use
	CommandLineRunner run(EmployeeService employeeService) {
		return args -> {
			employeeService.saveEmployee(new Employee("Reeza Ahamed", "reeza", "test123", new ArrayList<>()));
			employeeService.saveEmployee(new Employee("John Doe", "johnny", "test456", new ArrayList<>()));
			employeeService.saveEmployee(new Employee("Will Smith", "smith", "test789", new ArrayList<>()));
			employeeService.saveEmployee(new Employee("Joe Root", "joe", "test135", new ArrayList<>()));
			employeeService.saveEmployee(new Employee("Kevin Pieterson", "kevin", "test179", new ArrayList<>()));
			
			employeeService.saveRole(new Role("ROLE_ADMIN"));
			employeeService.saveRole(new Role("ROLE_USER"));
			employeeService.saveRole(new Role("ROLE_MANAGER"));
			
			employeeService.addRoleToEmployee("reeza", "ROLE_ADMIN");
			employeeService.addRoleToEmployee("reeza", "ROLE_USER");
			employeeService.addRoleToEmployee("reeza", "ROLE_MANAGER");
			employeeService.addRoleToEmployee("johnny", "ROLE_USER");
			employeeService.addRoleToEmployee("smith", "ROLE_ADMIN");
			employeeService.addRoleToEmployee("joe", "ROLE_MANAGER");
			employeeService.addRoleToEmployee("kevin", "ROLE_USER");
		};
	}

}
