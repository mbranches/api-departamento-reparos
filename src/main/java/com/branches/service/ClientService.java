package com.branches.service;

import com.branches.exception.NotFoundException;
import com.branches.model.Address;
import com.branches.model.Client;
import com.branches.repository.ClientRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ClientService {
    private final ClientRepository repository;
    private final AddressService addressService;

    public List<Client> findAll(String firstName) {
        return firstName == null ? repository.findAll() : repository.findByNameContaining(firstName);
    }

    public Client findByIdOrElseThrowsNotFoundException(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new NotFoundException("Client not Found"));
    }

    public Client save(Client clientToSave) {
        Address address = clientToSave.getAddress();

        Optional<Address> addressSearched = addressService.findAddress(address);
        Address addressSaved = addressSearched.orElseGet(() -> addressService.save(address));
        clientToSave.setAddress(addressSaved);

        clientToSave.getPhones()
            .forEach(phone -> phone.setClient(clientToSave));

        return repository.save(clientToSave);
    }
}
