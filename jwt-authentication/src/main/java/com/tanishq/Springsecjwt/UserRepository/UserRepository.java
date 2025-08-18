package com.tanishq.Springsecjwt.UserRepository;

import com.tanishq.Springsecjwt.Model.Users;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<Users, Long> {
    Optional<Users> findByUsername(String username);
}