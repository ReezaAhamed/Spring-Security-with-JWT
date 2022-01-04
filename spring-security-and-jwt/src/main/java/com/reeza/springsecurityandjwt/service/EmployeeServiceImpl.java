package com.reeza.springsecurityandjwt.service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.reeza.springsecurityandjwt.entity.Employee;
import com.reeza.springsecurityandjwt.entity.Role;
import com.reeza.springsecurityandjwt.repository.EmployeeRepository;
import com.reeza.springsecurityandjwt.repository.RoleRepository;

import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j;

@Service
@RequiredArgsConstructor // when you have final fields
@Transactional
//@Slf4j // To check what's happening in the log
public class EmployeeServiceImpl implements EmployeeService, UserDetailsService {
	
	private final EmployeeRepository employeeRepository;
	private final RoleRepository roleRepository;
	private final PasswordEncoder passwordEncoder;
	
	
	@Override // the method we need to override when UserDetailsService is used, this is the method spring uses to load the users from database or whatever it might be.
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		
		 // get the employee
		Employee employee = employeeRepository.findByUsername(username);
		
		if(employee == null) {
			// log.error("User not found in the Database");
			throw new UsernameNotFoundException("User not found in the Database");
		}
		
		// log.info("User found in the Database : {}, username"); 
		Collection<SimpleGrantedAuthority> authorities = new ArrayList<>(); //authorities should be any collection which implemets GrantedAuthority, ...SimpleGrantedAuthority implements GrantedAuthority
		// let's fetch the roles from employee to user
		employee.getRoles().forEach(role -> {
			authorities.add(new SimpleGrantedAuthority(role.getName())); // loop through each role and add them to authorities
		});
		
		return new User(employee.getName(), employee.getPassword(), authorities); // we need to set the authorities which we will do in previous 3 lines
		
	}

	@Override
	public Employee saveEmployee(Employee employee) {
		// log.info("Saving new employee {} to the Database", employee.getName());
		employee.setPassword(passwordEncoder.encode(employee.getPassword()));
		return employeeRepository.save(employee);
	}

	@Override
	public Role saveRole(Role role) {
		// log.info("Saving new role {} to the Database", role.getName());
		return roleRepository.save(role);
	}

	@Override
	public Employee getEmployee(String username) {
		// log.info("Fetching employee {}", username);
		return employeeRepository.findByUsername(username);
	}

	@Override
	public List<Employee> getEmployees() {
		// log.info("Fetching all employees");
		return employeeRepository.findAll();
	}

	@Override
	public void addRoleToEmployee(String username, String roleName) {
		// log.info("Adding role {} to employee {}", username, roleName);
		Employee employee = employeeRepository.findByUsername(username);
		Role role = roleRepository.findByName(roleName);
		employee.getRoles().add(role);

	}

}
