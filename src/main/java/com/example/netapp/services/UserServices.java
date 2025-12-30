package com.example.netapp.services;

import java.util.Optional;

import org.springframework.stereotype.Service;

import com.example.netapp.entity.UserEntity;
import com.example.netapp.repository.UserRepository;

@Service
public class UserServices {

	private UserRepository userRepo;
	
	public UserServices(UserRepository userRepo) {
		this.userRepo = userRepo;
	}
	
	public UserEntity getUserById(Long id) {
	    return userRepo.findById(id).orElse(null);
	}
}
