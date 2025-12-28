package com.example.netapp.services;

import org.springframework.stereotype.Service;

import com.example.netapp.repository.UserRepository;

@Service
public class UserServices {

	private UserRepository userRepo;
	
	public UserServices(UserRepository userRepo) {
		this.userRepo = userRepo;
	}
}
