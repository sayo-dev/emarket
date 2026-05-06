package org.example.e_market.entity;

import jakarta.persistence.*;
import lombok.*;
import org.example.e_market.entity.enums.AccountType;
import org.example.e_market.entity.vendor.Vendor;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    private Vendor vendor;

    private String name;

    @Column(unique = true)
    private String email;

    private String password;

    @Enumerated(EnumType.STRING)
    private AccountType accountType;

    @Column(updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    private boolean isVerified = false;

    private LocalDateTime deletedAt;
}
