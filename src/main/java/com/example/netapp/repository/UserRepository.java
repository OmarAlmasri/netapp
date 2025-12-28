package com.example.netapp.repository;

import java.util.Optional;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import com.example.netapp.entity.UserEntity;

@Repository
public interface UserRepository extends CrudRepository<UserEntity,Long>{
    Optional<UserEntity> findByEmail(String email);

}
