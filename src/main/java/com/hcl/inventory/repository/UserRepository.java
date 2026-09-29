package com.hcl.inventory.repository;

import com.hcl.inventory.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User,Long> {
    // JpaRepository -> I have to give table name and primary key
    Optional<User> findByEmail(String email);
}
