package com.example.netapp.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import com.example.netapp.entity.UserEntity;
import com.example.netapp.entity.UserRole;

@Repository
public interface UserRepository extends CrudRepository<UserEntity,Long>{
    Optional<UserEntity> findByEmail(String email);

    List<UserEntity> findByRole(UserRole role);
}
