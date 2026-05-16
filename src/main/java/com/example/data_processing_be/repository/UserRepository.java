package com.example.data_processing_be.repository;

import com.example.data_processing_be.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.*;
import java.io.*;

public interface UserRepository extends JpaRepository<User,UUID > {
  Optional<User> findByEmail(String email);

  boolean existsByEmail(String email);
}
