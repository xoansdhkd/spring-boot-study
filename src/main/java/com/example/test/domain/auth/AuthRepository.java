package com.example.test.domain.auth;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface AuthRepository extends JpaRepository<Auth, Long> {

    Auth findByUsername(String username);

}
