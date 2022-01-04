package com.reeza.springsecurityandjwt.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.reeza.springsecurityandjwt.entity.Role;

public interface RoleRepository extends JpaRepository<Role, Integer> {
	
	Role findByName(String name);

}
