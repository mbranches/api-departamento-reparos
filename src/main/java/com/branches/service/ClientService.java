package com.branches.service;

import com.branches.model.Client;
import com.branches.repository.ClientRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ClientService {
    private final ClientRepository repository;
    public List<Client> findAll(String firstName) {
        return firstName == null ? repository.findAll() : repository.findByNameContaining(firstName);
    }
}
