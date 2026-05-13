package org.example.e_market.repositories;

import jakarta.validation.constraints.Email;
import org.example.e_market.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, String> {
    Optional<User> findByEmailIgnoreCase(String email);

    @Query("SELECT u.vendor.id FROM User u WHERE LOWER(u.email) = LOWER(?1)")
    Optional<String> findVendorIdByEmail(String email);

    boolean existsByEmail(String email);

    boolean existsByEmailAndIsVerifiedTrue(@Email String email);
}
