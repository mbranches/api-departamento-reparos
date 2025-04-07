package com.branches.controller;

import com.branches.exception.NotFoundException;
import com.branches.request.ClientPostRequest;
import com.branches.response.ClientGetResponse;
import com.branches.service.ClientService;
import com.branches.service.VehicleService;
import com.branches.utils.ClientUtils;
import com.branches.utils.FileUtils;
import com.branches.utils.VehicleUtils;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.ArgumentMatchers;
import org.mockito.BDDMockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

@WebMvcTest(controllers = ClientController.class)
@Import(FileUtils.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class ClientControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @MockitoBean
    private ClientService service;
    @MockitoBean
    private VehicleService vehicleService;
    @Autowired
    private FileUtils fileUtils;
    private final String URL = "/v1/clients";
    private List<ClientGetResponse> clientGetResponseList;

    @BeforeEach
    void init() {
        clientGetResponseList = ClientUtils.newClientGetResponseList();
    }

    @Test
    @DisplayName("GET /v1/clients returns all clients when the given argument is null")
    @Order(1)
    void findAll_ReturnsAllClients_WhenGivenArgumentIsNull() throws Exception {
        BDDMockito.when(service.findAll(null)).thenReturn(clientGetResponseList);
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
        BDDMockito.when(service.findAll(nameToSearch)).thenReturn(List.of(clientGetResponseList.getFirst()));
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
        BDDMockito.when(service.findAll(randomName)).thenReturn(Collections.emptyList());
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
        ClientGetResponse expectedClient = clientGetResponseList.getFirst();
        long idToSearch = 1L;

        BDDMockito.when(service.findById(idToSearch)).thenReturn(expectedClient);
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

        BDDMockito.when(service.findById(randomId)).thenThrow(new NotFoundException("Client not Found"));
        String expectedResponse = fileUtils.readResourceFile("client/get-client-by-id-404.json");

        mockMvc.perform(MockMvcRequestBuilders.get(URL + "/{id}", randomId))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect(MockMvcResultMatchers.content().json(expectedResponse));
    }

    @Test
    @DisplayName("GET /v1/clients/4/vehicles Returns all client vehicles when successful")
    @Order(6)
    void findVehiclesByClientId_ReturnsAllClientVehicles_WhenSuccessful() throws Exception {
        long clientId = 4L;
        String expectedResponse = fileUtils.readResourceFile("vehicle/get-vehicles-by-client-id-200.json");

        BDDMockito.when(vehicleService.findByClientId(clientId)).thenReturn(VehicleUtils.newVehicleClientGetReponseList());

        mockMvc.perform(MockMvcRequestBuilders.get(URL + "/{clientId}/vehicles", clientId))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().json(expectedResponse));
    }

    @Test
    @DisplayName("GET /v1/clients/1/vehicles returns an empty list when client doesn't have vehicles")
    @Order(7)
    void findVehiclesByClientId_ReturnsEmptyList_WhenClientDoesNotHaveVehicles() throws Exception {
        long clientId = 1L;
        String expectedResponse = fileUtils.readResourceFile("vehicle/get-empty-list-by-client-id-200.json");

        BDDMockito.when(vehicleService.findByClientId(clientId)).thenReturn(Collections.emptyList());

        mockMvc.perform(MockMvcRequestBuilders.get(URL + "/{clientId}/vehicles", clientId))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().json(expectedResponse));
    }

    @Test
    @DisplayName("GET /v1/clients/488/vehicles throws NotFoundException when client is not found")
    @Order(8)
    void findVehiclesByClientId_ThrowsNotFoundException_WhenClientIsNotFound() throws Exception {
        long randomId = 1L;
        String expectedResponse = fileUtils.readResourceFile("vehicle/get-vehicles-by-client-id-404.json");

        BDDMockito.when(vehicleService.findByClientId(randomId)).thenThrow(new NotFoundException("Client not Found"));

        mockMvc.perform(MockMvcRequestBuilders.get(URL + "/{clientId}/vehicles", randomId))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect(MockMvcResultMatchers.content().json(expectedResponse));
    }

    @Test
    @DisplayName("POST /v1/clients returns saved client when successful")
    @Order(9)
    void save_ReturnsSavedClient_WhenGivenSuccessful() throws Exception {

        BDDMockito.when(service.save(ArgumentMatchers.any(ClientPostRequest.class))).thenReturn(ClientUtils.newClientPostResponse());

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

    @ParameterizedTest
    @MethodSource("postClientBadRequestSource")
    @DisplayName("save return BadRequest when fields are invalid")
    @Order(10)
    void save_ReturnsBadRequest_WhenFieldAreInvalid(String fileName, List<String> expectedErrors) throws Exception {
        String request = fileUtils.readResourceFile("client/%s".formatted(fileName));

        MvcResult mvcResult = mockMvc.perform(
                        MockMvcRequestBuilders.post(URL)
                                .content(request)
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andReturn();

        Exception exception = mvcResult.getResolvedException();

        Assertions.assertThat(exception.getMessage())
                .isNotNull()
                .contains(expectedErrors);
    }

    private static Stream<Arguments> postClientBadRequestSource() {
        String nameRequiredError = "The field name is required";
        String lastNameRequiredError = "The field lastName is required";

        List<String> expectedErrors = List.of(nameRequiredError, lastNameRequiredError);
        return Stream.of(
                Arguments.of("post-request-client-empty-fields-400.json", expectedErrors),
                Arguments.of("post-request-client-blank-fields-400.json", expectedErrors)
        );
    }    
}