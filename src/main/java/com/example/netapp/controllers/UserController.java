package com.example.netapp.controllers;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.netapp.services.UserServices;

@RestController
@RequestMapping("/api/v1/users")
public class UserController {

	private UserServices userServices;
	
	public UserController(UserServices userServices) {
		this.userServices = userServices;
	}
	//TODO:
	//1- login function  
	//2- signup function
	//3- get user by id 
	//4- delete user by id ( we need to add a check if the user deleting the account has the same id ).
/*
	@PostMapping("/login")
	public User login(@RequestParam User user) {
		
	}
*/
}
