package com.reeza.springsecurityandjwt.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.reeza.springsecurityandjwt.entity.Employee;

public interface EmployeeRepository extends JpaRepository<Employee, Integer> {

	Employee findByUsername(String username); //spring data jpa is smart enough to interpret as select the user by the username that we pass 
}
