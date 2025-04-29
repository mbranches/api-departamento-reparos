package com.branches.service;

import com.branches.exception.NotFoundException;
import com.branches.mapper.ClientMapper;
import com.branches.mapper.PersonMapper;
import com.branches.model.Address;
import com.branches.model.Client;
import com.branches.model.Person;
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
    private final PersonMapper personMapper;
    private final ClientMapper mapper;
    private final AddressService addressService;
    private final PhoneService phoneService;
    private final PersonService personService;

    public List<ClientGetResponse> findAll(String firstName) {
        List<Client> response = firstName == null ? repository.findAll() : repository.findAllByPerson_NameContaining(firstName);

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
        Person personToSave = personMapper.toPerson(postRequest);

        Address address = personToSave.getAddress();
        if (address != null) {
            Optional<Address> addressSearched = addressService.findAddress(address);

            Address addressSaved = addressSearched.orElseGet(() -> addressService.save(address));
            personToSave.setAddress(addressSaved);
        }

        List<Phone> phones = personToSave.getPhones();
        if (phones != null) phones.forEach(phone -> {
            phoneService.assertPhoneDoesNotExists(phone);
            phone.setPerson(personToSave);
        });

        Person person = personService.save(personToSave);

        Client clientToSave = Client.builder().email(postRequest.getEmail()).person(person).build();

        Client client = repository.save(clientToSave);

        return mapper.toClientPostResponse(client);
    }

    public void deleteById(Long id) {
        Client clientToDelete = findByIdOrThrowsNotFoundException(id);

        repository.delete(clientToDelete);
    }
}
