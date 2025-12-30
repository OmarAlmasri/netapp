package com.example.netapp.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.netapp.dto.responses.ErrorResponse;
import com.example.netapp.entity.UserEntity;
import com.example.netapp.exceptions.NotFoundException;
import com.example.netapp.services.UserServices;

@RestController
@RequestMapping("/api/v1/users")
public class UserController {

	private UserServices userServices;
	
	public UserController(UserServices userServices) {
		this.userServices = userServices;
	}
	//TODO:
	//3- get user by id 
	//4- delete user by id ( we need to add a check if the user deleting the account has the same id ).
	@PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
	@GetMapping("/{id}")
	public ResponseEntity<?> get_user(@PathVariable Long id) {
		UserEntity user = userServices.getUserById(id);
		if(user == null) {
			throw new NotFoundException("user not found");
		}
		return ResponseEntity.ok(user);
	}
}
