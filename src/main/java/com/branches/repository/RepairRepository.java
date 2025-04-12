package com.branches.repository;

import com.branches.model.Client;
import com.branches.model.Repair;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface RepairRepository extends JpaRepository<Repair, Long> {
    List<Repair> findByEndDateGreaterThanEqual(LocalDate date);

    List<Repair> findAllByClient(Client client);
}
