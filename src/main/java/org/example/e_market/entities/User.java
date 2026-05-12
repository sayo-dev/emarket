package org.example.e_market.entities;

import jakarta.persistence.*;
import lombok.*;
import org.example.e_market.entities.enums.AccountType;
import org.example.e_market.entities.vendor.Vendor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "users")
@EntityListeners(AuditingEntityListener.class)
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @ManyToOne(fetch = FetchType.LAZY)
    private Vendor vendor;

    private String name;

    @Column(unique = true)
    private String email;

    private String password;

    @Enumerated(EnumType.STRING)
    private AccountType accountType;

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    private boolean isVerified = false;

    private LocalDateTime deletedAt;

    public String getVendorId() {
        if (vendor != null) {
            return vendor.getId();
        }
        return null;
    }
}
