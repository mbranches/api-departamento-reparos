package com.branches.service;

import com.branches.exception.NotFoundException;
import com.branches.mapper.ClientMapper;
import com.branches.model.Address;
import com.branches.model.Client;
import com.branches.repository.ClientRepository;
import com.branches.request.ClientPostRequest;
import com.branches.response.ClientGetResponse;
import com.branches.response.ClientPostResponse;
import com.branches.utils.ClientUtils;
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
    private ClientMapper mapper;
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
        String nameToSearch = clientToBeFound.getName();
        List<ClientGetResponse> expectedResponse = List.of(clientToBeFound);

        List<Client> expectedResponseRepository = List.of(clientList.getFirst());

        BDDMockito.when(repository.findAllByNameContaining(nameToSearch)).thenReturn(expectedResponseRepository);
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

        BDDMockito.when(repository.findAllByNameContaining(randomName)).thenReturn(Collections.emptyList());
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
        Client clientToSave = ClientUtils.newClientToSave();
        ClientPostRequest clientPostRequest = ClientUtils.newClientPostRequest();

        Address clientAddress = clientToSave.getAddress();

        ClientPostResponse expectedResponse = ClientUtils.newClientPostResponse();

        BDDMockito.when(mapper.toClient(clientPostRequest)).thenReturn(clientToSave);
        BDDMockito.when(addressService.findAddress(clientAddress)).thenReturn(Optional.of(clientAddress));
        BDDMockito.when(repository.save(clientToSave)).thenReturn(clientToSave);
        BDDMockito.when(mapper.toClientPostResponse(clientToSave)).thenReturn(expectedResponse);

        ClientPostResponse response = service.save(clientPostRequest);

        Assertions.assertThat(response)
                .isNotNull()
                .isEqualTo(expectedResponse);
    }
}