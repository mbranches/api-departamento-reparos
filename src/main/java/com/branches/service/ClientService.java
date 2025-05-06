package com.branches.service;

import com.branches.exception.BadRequestException;
import com.branches.exception.NotFoundException;
import com.branches.mapper.ClientMapper;
import com.branches.mapper.PersonMapper;
import com.branches.model.Client;
import com.branches.model.Person;
import com.branches.repository.ClientRepository;
import com.branches.request.ClientPostRequest;
import com.branches.request.ClientPutRequest;
import com.branches.response.ClientGetResponse;
import com.branches.response.ClientPostResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

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

        assertEmailDoesNotExists(postRequest.getEmail());

        Person person = personService.save(personToSave);

        Client clientToSave = Client.builder()
                .email(postRequest.getEmail())
                .person(person)
                .build();

        Client client = repository.save(clientToSave);

        return mapper.toClientPostResponse(client);
    }

    @Transactional
    public void update(Long id, ClientPutRequest putRequest) {
        if (!id.equals(putRequest.getId())) throw new BadRequestException("The ID in the request body (%s) does not match the ID in the URL (%s)".formatted(putRequest.getId(), id));

        Client clientNotUpdated = findByIdOrThrowsNotFoundException(id);

        assertEmailDoesNotExists(putRequest.getEmail(), id);

        Long personId = clientNotUpdated.getPerson().getId();
        Person personToUpdate = personMapper.toPerson(putRequest);
        personToUpdate.setId(personId);

        Person personUpdated = personService.update(personToUpdate);

        Client clientToUpdate = Client.builder()
                .id(putRequest.getId())
                .person(personUpdated)
                .email(putRequest.getEmail())
                .build();

        repository.save(clientToUpdate);
    }

    public void deleteById(Long id) {
        Client clientToDelete = findByIdOrThrowsNotFoundException(id);

        repository.delete(clientToDelete);
    }

    public void assertEmailDoesNotExists(String email) {
        repository.findByEmail(email)
                .ifPresent(ClientService::throwsEmailAlreadyExistsException);
    }

    public void assertEmailDoesNotExists(String email, Long id) {
        repository.findByEmailAndIdNot(email, id)
                .ifPresent(ClientService::throwsEmailAlreadyExistsException);
    }

    private static void throwsEmailAlreadyExistsException(Client client) {
        throw new BadRequestException("Email '%s' belongs to another person".formatted(client.getEmail()));
    }
}
