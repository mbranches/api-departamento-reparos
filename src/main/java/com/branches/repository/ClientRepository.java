package com.branches.repository;

import com.branches.model.Client;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ClientRepository extends JpaRepository<Client, Long> {
    List<Client> findAllByPerson_NameContaining(String personName);

    Optional<Client> findByEmail(String email);

    Optional<Client> findByEmailAndIdNot(String email, Long id);
}
