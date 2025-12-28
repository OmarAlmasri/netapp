package com.example.netapp.dto.responses;

import com.example.netapp.entity.UserEntity;

public record SignupResponse(String status , UserEntity user) {

}
