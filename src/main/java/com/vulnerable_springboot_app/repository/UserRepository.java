package com.vulnerable_springboot_app.repository;

import com.example.vulnerableapp.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    
    @Query(value = "SELECT * FROM user WHERE name = ?1", nativeQuery = true)
    User findByNameQuery(String name);  // SQL Injection vulnerability
}
