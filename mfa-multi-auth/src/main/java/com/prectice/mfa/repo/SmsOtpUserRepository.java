package com.prectice.mfa.repo;


import com.prectice.mfa.Model.SmsOtpUser;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SmsOtpUserRepository extends JpaRepository<SmsOtpUser, Long> {
    Optional<SmsOtpUser> findByUsername(String username);
}
