package com.reeza.springsecurityandjwt.entity;

import java.util.ArrayList;
import java.util.Collection;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;

//import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data //getters and setters
@NoArgsConstructor
//@AllArgsConstructor // cuz id is created automatically using auto increment (IDENTITY)
public class Employee {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;
	private String name;
	private String username; //username/email same, in the frontend, just tell them to enter username/password
	private String password;
	
	@ManyToMany(fetch = FetchType.EAGER) //when we load (featch) the employee we also should load the roles he has, that's what EAGER does
	private Collection<Role> roles = new ArrayList<>();

	public Employee(String name, String username, String password, Collection<Role> roles) {
		this.name = name;
		this.username = username;
		this.password = password;
		this.roles = roles;
	}
	
	
	
}
