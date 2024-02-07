package com.servicehealthcheck.sericeMonitor.repository;

import com.servicehealthcheck.sericeMonitor.model.User;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface UserRepository extends MongoRepository<User, String> {

    User findByEmail(String email) ;
    
  }