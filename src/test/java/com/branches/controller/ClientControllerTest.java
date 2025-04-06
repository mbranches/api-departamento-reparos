package com.branches.controller;

import com.branches.mapper.ClientMapperImpl;
import com.branches.model.Address;
import com.branches.model.Client;
import com.branches.model.Phone;
import com.branches.repository.ClientRepository;
import com.branches.service.AddressService;
import com.branches.service.ClientService;
import com.branches.utils.AddressUtils;
import com.branches.utils.ClientUtils;
import com.branches.utils.FileUtils;
import org.junit.jupiter.api.*;
import org.mockito.ArgumentMatchers;
import org.mockito.BDDMockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@WebMvcTest(controllers = ClientController.class)
@Import({ClientService.class, ClientMapperImpl.class, FileUtils.class})
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class ClientControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @MockitoBean
    private ClientRepository repository;
    @MockitoBean
    private AddressService addressService;
    @Autowired
    private FileUtils fileUtils;
    private final String URL = "/v1/clients";
    private List<Client> clientList;

    @BeforeEach
    void init() {
        clientList = ClientUtils.newClientList();
    }

    @Test
    @DisplayName("GET /v1/clients returns all clients when the given argument is null")
    @Order(1)
    void findAll_ReturnsAllClients_WhenGivenArgumentIsNull() throws Exception {
        BDDMockito.when(repository.findAll()).thenReturn(clientList);
        String expectedResponse = fileUtils.readResourceFile("client/get-clients-null-name-200.json");

        mockMvc.perform(MockMvcRequestBuilders.get(URL))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().json(expectedResponse));
    }

    @Test
    @DisplayName("GET /v1/clients returns the found clients when the argument is given")
    @Order(2)
    void findAll_ReturnsFoundClients_WhenArgumentIsGiven() throws Exception {
        String nameToSearch = "Marcus";
        BDDMockito.when(repository.findAllByNameContaining(nameToSearch)).thenReturn(List.of(clientList.getFirst()));
        String expectedResponse = fileUtils.readResourceFile("client/get-clients-marcus-name-200.json");

        mockMvc.perform(MockMvcRequestBuilders.get(URL).param("firstName", nameToSearch))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().json(expectedResponse));
    }

    @Test
    @DisplayName("GET /v1/clients returns an empty list when the given argument is not found")
    @Order(3)
    void findAll_ReturnsEmptyList_WhenGivenArgumentIsNotFound() throws Exception {
        String randomName = "nameNotRegistered";
        BDDMockito.when(repository.findAllByNameContaining(randomName)).thenReturn(Collections.emptyList());
        String expectedResponse = fileUtils.readResourceFile("client/get-clients-nameNotRegistered-name-200.json");

        mockMvc.perform(MockMvcRequestBuilders.get(URL).param("firstName", randomName))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().json(expectedResponse));
    }

    @Test
    @DisplayName("GET /v1/clients/1 returns found client when successful")
    @Order(4)
    void findById_ReturnsFoundClient_WhenSuccessful() throws Exception {
        Client expectedClient = clientList.getFirst();
        long idToSearch = expectedClient.getId();

        BDDMockito.when(repository.findById(idToSearch)).thenReturn(Optional.of(expectedClient));
        String expectedResponse = fileUtils.readResourceFile("client/get-client-by-id-200.json");

        mockMvc.perform(MockMvcRequestBuilders.get(URL + "/{id}", idToSearch))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().json(expectedResponse));
    }

    @Test
    @DisplayName("GET /v1/clients/ throws NotFoundException when id is not found")
    @Order(5)
    void findById_ThrowsNotFoundException_WhenIdIsNotFound() throws Exception {
        long randomId = 131222L;

        BDDMockito.when(repository.findById(randomId)).thenReturn(Optional.empty());
        String expectedResponse = fileUtils.readResourceFile("client/get-client-by-id-404.json");

        mockMvc.perform(MockMvcRequestBuilders.get(URL + "/{id}", randomId))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect(MockMvcResultMatchers.content().json(expectedResponse));
    }
    @Test
    @DisplayName("POST /v1/clients returns saved client when successful")
    @Order(6)
    void save_ReturnsSavedClient_WhenGivenSuccessful() throws Exception {
        Client clientSaved = ClientUtils.newClientToSave();

        Address addressToSave = AddressUtils.newAddressToSave();
        addressToSave.setId(null);

        BDDMockito.when(addressService.findAddress(addressToSave)).thenReturn(Optional.of(AddressUtils.newAddressToSave()));
        BDDMockito.when(repository.save(ArgumentMatchers.any(Client.class))).thenReturn(clientSaved);

        String request = fileUtils.readResourceFile("client/post-request-client-200.json");
        String expectedResponse = fileUtils.readResourceFile("client/post-response-client-201.json");

        mockMvc.perform(
                    MockMvcRequestBuilders.post(URL)
                            .content(request)
                            .contentType(MediaType.APPLICATION_JSON_VALUE)
                )
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.content().json(expectedResponse));
    }
    
}