package com.trekconnect.auth.repository;

import com.trekconnect.auth.entity.UserCredentials;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserCredentialsRepository extends JpaRepository<UserCredentials, String> {

    Optional<UserCredentials> findByEmail(String email);

    boolean existsByEmail(String email);
}
