package com.reeza.springsecurityandjwt.service;

import java.util.List;

import com.reeza.springsecurityandjwt.entity.Employee;
import com.reeza.springsecurityandjwt.entity.Role;

public interface EmployeeService {
	
	Employee saveEmployee(Employee employee);
	Role saveRole(Role role);
	Employee getEmployee(String username);
	List<Employee> getEmployees();
	void addRoleToEmployee(String username, String roleName);

}
