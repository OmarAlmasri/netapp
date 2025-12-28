package com.example.netapp.dto.responses;

import com.example.netapp.entity.UserEntity;

public record LoginResponse(UserEntity user , String token) {

}
