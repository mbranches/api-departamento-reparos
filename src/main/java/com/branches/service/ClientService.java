package com.branches.service;

import com.branches.exception.NotFoundException;
import com.branches.mapper.ClientMapper;
import com.branches.model.Address;
import com.branches.model.Client;
import com.branches.model.Phone;
import com.branches.repository.ClientRepository;
import com.branches.request.ClientPostRequest;
import com.branches.response.ClientGetResponse;
import com.branches.response.ClientPostResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ClientService {
    private final ClientRepository repository;
    private final ClientMapper mapper;
    private final AddressService addressService;

    public List<ClientGetResponse> findAll(String firstName) {
        List<Client> response = firstName == null ? repository.findAll() : repository.findAllByNameContaining(firstName);
        return mapper.toClientGetResponseList(response);
    }

    public ClientGetResponse findById(Long id) {
        Client clientFound = findByIdOrThrowsNotFoundException(id);

        return mapper.toClientGetResponse(clientFound);
    }

    public Client findByIdOrThrowsNotFoundException(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new NotFoundException("Client not Found"));
    }

    @Transactional
    public ClientPostResponse save(ClientPostRequest postRequest) {
        Client clientToSave = mapper.toClient(postRequest);

        Address address = clientToSave.getAddress();
        if (address != null) {
            Optional<Address> addressSearched = addressService.findAddress(address);

            Address addressSaved = addressSearched.orElseGet(() -> addressService.save(address));
            clientToSave.setAddress(addressSaved);
        }

        List<Phone> phones = clientToSave.getPhones();
        if (phones != null) phones.forEach(phone -> phone.setClient(clientToSave));

        Client response = repository.save(clientToSave);

        return mapper.toClientPostResponse(response);
    }

    public void deleteById(Long id) {
        Client clientToDelete = findByIdOrThrowsNotFoundException(id);

        repository.delete(clientToDelete);
    }
}
