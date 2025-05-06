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
import com.branches.utils.ClientUtils;
import com.branches.utils.PersonUtils;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.BDDMockito;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class ClientServiceTest {
    @InjectMocks
    private ClientService service;
    @Mock
    private ClientRepository repository;
    @Mock
    private AddressService addressService;
    @Mock
    private PhoneService phoneService;
    @Mock
    private PersonService personService;
    @Mock
    private ClientMapper mapper;
    @Mock
    private PersonMapper personMapper;
    private List<ClientGetResponse> clientGetResponseList;
    private List<Client> clientList;

    @BeforeEach
    void init() {
        clientGetResponseList = ClientUtils.newClientGetResponseList();
        clientList = ClientUtils.newClientList();
    }

    @Test
    @DisplayName("findAll returns all clients when the given argument is null")
    @Order(1)
    void findAll_ReturnsAllClients_WhenGivenArgumentIsNull() {
        BDDMockito.when(repository.findAll()).thenReturn(clientList);
        BDDMockito.when(mapper.toClientGetResponseList(ArgumentMatchers.anyList())).thenReturn(clientGetResponseList);

        List<ClientGetResponse> response = service.findAll(null);

        Assertions.assertThat(response)
                .isNotNull()
                .isNotEmpty()
                .containsExactlyElementsOf(clientGetResponseList);
    }

    @Test
    @DisplayName("findAll returns the found clients when the argument is given")
    @Order(2)
    void findAll_ReturnsFoundClients_WhenArgumentIsGiven() {
        ClientGetResponse clientToBeFound = clientGetResponseList.getFirst();
        String nameToSearch = clientToBeFound.getPerson().getName();
        List<ClientGetResponse> expectedResponse = List.of(clientToBeFound);

        List<Client> expectedResponseRepository = List.of(clientList.getFirst());

        BDDMockito.when(repository.findAllByPerson_NameContaining(nameToSearch)).thenReturn(expectedResponseRepository);
        BDDMockito.when(mapper.toClientGetResponseList(ArgumentMatchers.anyList())).thenReturn(expectedResponse);

        List<ClientGetResponse> response = service.findAll(nameToSearch);

        Assertions.assertThat(response)
                .isNotNull()
                .isNotEmpty()
                .containsExactlyElementsOf(expectedResponse);
    }

    @Test
    @DisplayName("findAll returns an empty list when the given argument is not found")
    @Order(3)
    void findAll_ReturnsEmptyList_WhenGivenArgumentIsNotFound() {
        String randomName = "name invalid";

        BDDMockito.when(repository.findAllByPerson_NameContaining(randomName)).thenReturn(Collections.emptyList());
        BDDMockito.when(mapper.toClientGetResponseList(ArgumentMatchers.anyList())).thenReturn(Collections.emptyList());

        List<ClientGetResponse> response = service.findAll(randomName);

        Assertions.assertThat(response)
                .isNotNull()
                .isEmpty();
    }

    @Test
    @DisplayName("findById returns found client when successful")
    @Order(4)
    void findById_ReturnsFoundClient_WhenSuccessful() {
        Client expectedResponseRepository = clientList.getFirst();
        Long idToSearch = expectedResponseRepository.getId();

        ClientGetResponse expectedResponse = clientGetResponseList.getFirst();

        BDDMockito.when(repository.findById(idToSearch)).thenReturn(Optional.of(expectedResponseRepository));
        BDDMockito.when(mapper.toClientGetResponse(expectedResponseRepository)).thenReturn(expectedResponse);

        ClientGetResponse response = service.findById(idToSearch);

        Assertions.assertThat(response)
                .isNotNull()
                .isEqualTo(expectedResponse);
    }

    @Test
    @DisplayName("findById throws NotFoundException when id is not found")
    @Order(5)
    void findById_ThrowsNotFoundException_WhenIdIsNotFound() {
        Long randomId = 4445511L;

        BDDMockito.when(repository.findById(randomId)).thenReturn(Optional.empty());

        Assertions.assertThatThrownBy(() -> service.findById(randomId))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("Client not Found");
    }

    @Test
    @DisplayName("save returns saved client when successful")
    @Order(6)
    void save_ReturnsSavedClient_WhenGivenSuccessful() {
        Person personToSave = PersonUtils.newPersonToSave();
        Person personSaved = PersonUtils.newPersonSaved();

        Client clientToSave = ClientUtils.newClientToSave();
        Client clientSaved = ClientUtils.newClientSaved();
        ClientPostRequest clientPostRequest = ClientUtils.newClientPostRequest();

        ClientPostResponse expectedResponse = ClientUtils.newClientPostResponse();

        BDDMockito.when(personMapper.toPerson(clientPostRequest)).thenReturn(personToSave);
        BDDMockito.when(personService.save(personToSave)).thenReturn(personSaved);
        BDDMockito.when(repository.save(clientToSave)).thenReturn(clientSaved);
        BDDMockito.when(mapper.toClientPostResponse(clientSaved)).thenReturn(expectedResponse);

        ClientPostResponse response = service.save(clientPostRequest);

        Assertions.assertThat(response)
                .isNotNull()
                .isEqualTo(expectedResponse);
    }

    @Test
    @DisplayName("save throws BadRequestException when the phone already exists")
    @Order(7)
    void save_ThrowsBadRequestException_WhenThePhoneAlreadyExists() {
        Person personToSave = PersonUtils.newPersonToSave().withId(null);
        ClientPostRequest clientPostRequest = ClientUtils.newClientPostRequest();

        BDDMockito.when(personMapper.toPerson(clientPostRequest)).thenReturn(personToSave);
        BDDMockito.doThrow(BadRequestException.class).when(personService).save(personToSave);

        Assertions.assertThatThrownBy(() -> service.save(clientPostRequest))
                .isInstanceOf(BadRequestException.class);
    }

    @Test
    @DisplayName("update updates client when successful")
    @Order(8)
    void update_UpdatesClient_WhenSuccessful() {
        Client clientNotUpdated = clientList.getFirst();

        Client clientToUpdate = ClientUtils.newClientToUpdate();

        ClientPutRequest clientPutRequest = ClientUtils.newClientPutRequest();
        Long idToUpdate = clientPutRequest.getId();

        Person personToUpdate = PersonUtils.newPersonToUpdate();

        BDDMockito.when(repository.findById(idToUpdate)).thenReturn(Optional.of(clientNotUpdated));
        BDDMockito.when(repository.findByEmailAndIdNot(clientPutRequest.getEmail(), idToUpdate)).thenReturn(Optional.empty());
        BDDMockito.when(personMapper.toPerson(clientPutRequest)).thenReturn(personToUpdate);
        BDDMockito.when(personService.update(personToUpdate)).thenReturn(personToUpdate);
        BDDMockito.when(repository.save(clientToUpdate)).thenReturn(clientToUpdate);

        Assertions.assertThatNoException()
                .isThrownBy(() -> service.update(idToUpdate, clientPutRequest));
    }

    @Test
    @DisplayName("update throws BadRequestException when the url id does not match the request body id")
    @Order(9)
    void update_ThrowsBadRequestException_WhenTheUrlIdDoesNotMatchTheRequestBodyId() {
        long randomId = 213123L;

        ClientPutRequest putRequest = ClientUtils.newClientPutRequest();

        Assertions.assertThatThrownBy(() -> service.update(randomId, putRequest))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("The ID in the request body (%s) does not match the ID in the URL (%s)".formatted(putRequest.getId(), randomId));
    }

    @Test
    @DisplayName("update throws NotFoundException when the client is not found")
    @Order(10)
    void update_ThrowsNotFoundException_WhenTheClientIsNotFound() {
        long randomId = 213123L;

        ClientPutRequest putRequest = ClientUtils.newClientPutRequest().withId(randomId);

        BDDMockito.when(repository.findById(randomId)).thenReturn(Optional.empty());

        Assertions.assertThatThrownBy(() -> service.update(randomId, putRequest))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("Client not Found");
    }

    @Test
    @DisplayName("update throws BadRequestException when the email to update does not belong to client")
    @Order(11)
    void update_ThrowsBadRequestException_WhenTheEmailToUpdateDoesNotBelongToClient() {
        Client clientEmailOwner = clientList.get(1);

        Client clientNotUpdated = clientList.getFirst();
        Long idToUpdate = clientNotUpdated.getId();

        ClientPutRequest clientPutRequest = ClientUtils.newClientPutRequest().withEmail(clientEmailOwner.getEmail());

        BDDMockito.when(repository.findById(idToUpdate)).thenReturn(Optional.of(clientNotUpdated));
        BDDMockito.when(repository.findByEmailAndIdNot(clientPutRequest.getEmail(), idToUpdate)).thenReturn(Optional.of(clientEmailOwner));

        Assertions.assertThatThrownBy(() -> service.update(idToUpdate, clientPutRequest))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("Email '%s' belongs to another person".formatted(clientPutRequest.getEmail()));
    }

    @Test
    @DisplayName("deleteById removes client when successful")
    @Order(12)
    void deleteById_RemovesClient_WhenSuccessful() {
        Client clientToDelete = clientList.getFirst();
        Long idToDelete = clientToDelete.getId();

        BDDMockito.when(repository.findById(idToDelete)).thenReturn(Optional.of(clientToDelete));
        BDDMockito.doNothing().when(repository).delete(ArgumentMatchers.any(Client.class));

        Assertions.assertThatCode(() -> service.deleteById(idToDelete))
                .doesNotThrowAnyException();
    }

    @Test
    @DisplayName("deleteById throws NotFoundException when given id is not found")
    @Order(13)
    void deleteById_ThrowsNotFoundException_WhenGivenIdIsNotFound() {
        Long randomId = 15512366L;

        BDDMockito.when(repository.findById(randomId)).thenReturn(Optional.empty());

        Assertions.assertThatThrownBy(() -> service.deleteById(randomId))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("Client not Found");
    }

    @Test
    @DisplayName("assertEmailDoesNotExists does nothing when email does not exists")
    @Order(14)
    void assertEmailDoesNotExists_DoesNothing_WhenTheEmailDoesNotExists() {
        ClientPostRequest clientPostRequest = ClientUtils.newClientPostRequest();
        String email = clientPostRequest.getEmail();

        BDDMockito.when(repository.findByEmail(email)).thenReturn(Optional.empty());

        Assertions.assertThatNoException().isThrownBy(() -> service.assertEmailDoesNotExists(email));
    }

    @Test
    @DisplayName("assertEmailDoesNotExists throws BadRequestException when the email already exists")
    @Order(15)
    void assertEmailDoesNotExists_DoesNothing_WhenTheEmailAlreadyExists() {
        Client client = ClientUtils.newClientList().getFirst();
        String savedEmail = client.getEmail();

        BDDMockito.when(repository.findByEmail(savedEmail)).thenReturn(Optional.of(client));

        Assertions.assertThatThrownBy(() -> service.assertEmailDoesNotExists(savedEmail))
                .isInstanceOf(BadRequestException.class);
    }
}