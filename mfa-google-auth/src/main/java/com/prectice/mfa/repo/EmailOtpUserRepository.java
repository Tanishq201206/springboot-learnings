package com.prectice.mfa.repo;

import com.prectice.mfa.Model.EmailOtpUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface EmailOtpUserRepository extends JpaRepository<EmailOtpUser, Long> {
    Optional<EmailOtpUser> findByUsername(String username);

}
