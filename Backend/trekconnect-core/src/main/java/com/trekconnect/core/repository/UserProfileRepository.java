package com.trekconnect.core.repository;

import com.trekconnect.core.entity.UserProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA Repository for UserProfile entity stored in main_db.
 */
@Repository
public interface UserProfileRepository extends JpaRepository<UserProfile, String> {
}
