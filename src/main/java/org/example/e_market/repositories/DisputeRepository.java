package org.example.e_market.repositories;

import org.example.e_market.entities.Dispute;
import org.example.e_market.entities.enums.DisputeStatus;
import org.example.e_market.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DisputeRepository extends JpaRepository<Dispute, Long> {
    List<Dispute> findByStatus(DisputeStatus status);
    List<Dispute> findByUser(User user);
}
