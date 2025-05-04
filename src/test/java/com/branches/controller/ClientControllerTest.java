package com.branches.controller;

import com.branches.exception.BadRequestException;
import com.branches.exception.NotFoundException;
import com.branches.model.Address;
import com.branches.model.Client;
import com.branches.model.Phone;
import com.branches.request.ClientPostRequest;
import com.branches.request.ClientPutRequest;
import com.branches.response.ClientGetResponse;
import com.branches.service.ClientService;
import com.branches.service.RepairService;
import com.branches.service.VehicleService;
import com.branches.utils.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
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

import java.util.ArrayList;
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
    @MockitoBean
    private RepairService repairService;
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
    @DisplayName("GET /v1/clients/131222 throws NotFoundException when id is not found")
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
    @DisplayName("GET /v1/clients/4/repairs Returns all client repairs when successful")
    @Order(9)
    void findRepairsByClientId_ReturnsAllClientRepairs_WhenSuccessful() throws Exception {
        long clientId = 4L;
        String expectedResponse = fileUtils.readResourceFile("repair/get-repairs-by-client-id-200.json");

        BDDMockito.when(repairService.findAllByClientId(clientId)).thenReturn(RepairUtils.newRepairGetResponseList());

        mockMvc.perform(MockMvcRequestBuilders.get(URL + "/{clientId}/repairs", clientId))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().json(expectedResponse));
    }

    @Test
    @DisplayName("GET /v1/clients/1/repairs returns an empty list when client doesn't have repairs")
    @Order(10)
    void findRepairsByClientId_ReturnsEmptyList_WhenClientDoesNotHaveRepairs() throws Exception {
        long clientId = 1L;
        String expectedResponse = fileUtils.readResourceFile("repair/get-empty-list-by-client-id-200.json");

        BDDMockito.when(vehicleService.findByClientId(clientId)).thenReturn(Collections.emptyList());

        mockMvc.perform(MockMvcRequestBuilders.get(URL + "/{clientId}/repairs", clientId))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().json(expectedResponse));
    }

    @Test
    @DisplayName("GET /v1/clients/488/repairs throws NotFoundException when client is not found")
    @Order(11)
    void findRepairsByClientId_ThrowsNotFoundException_WhenClientIsNotFound() throws Exception {
        long randomId = 1L;
        String expectedResponse = fileUtils.readResourceFile("repair/get-repairs-by-client-id-404.json");

        BDDMockito.when(repairService.findAllByClientId(randomId)).thenThrow(new NotFoundException("Client not Found"));

        mockMvc.perform(MockMvcRequestBuilders.get(URL + "/{clientId}/repairs", randomId))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect(MockMvcResultMatchers.content().json(expectedResponse));
    }

    @Test
    @DisplayName("POST /v1/clients returns saved client when successful")
    @Order(12)
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
    @DisplayName("POST /v1/clients return BadRequest when fields are invalid")
    @Order(13)
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
        String nameRequiredError = "The field 'name' is required";
        String lastNameRequiredError = "The field 'lastName' is required";
        String emailRequiredError = "The field 'email' is required";

        String emailNotValidError = "Email is not valid";

        List<String> expectedRequiredErrors = basicsRequiredErrors();
        return Stream.of(
                Arguments.of("post-request-client-empty-fields-400.json", expectedRequiredErrors),
                Arguments.of("post-request-client-blank-fields-400.json", expectedRequiredErrors),
                Arguments.of("post-request-client-invalid-email-400.json", List.of(emailNotValidError))
        );
    }

    @Test
    @DisplayName("PUT /v1/clients/1 updates client when successful")
    @Order(14)
    void update_UpdatesClient_WhenSuccessful() throws Exception {
        String request = fileUtils.readResourceFile("client/put-request-client-200.json");
        ClientPutRequest clientPutRequest = ClientUtils.newClientPutRequest();

        BDDMockito.doNothing().when(service).update(clientPutRequest.getId(), clientPutRequest);

        mockMvc.perform(MockMvcRequestBuilders.put(URL + "/{id}", clientPutRequest.getId())
                        .content(request)
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isNoContent());
    }

    @Test
    @DisplayName("PUT /v1/clients/1 throws BadRequestException when the url id does not match the request body id")
    @Order(14)
    void update_ThrowsBadRequestException_WhenTheUrlIdDoesNotMatchTheRequestBodyId() throws Exception {
        String request = fileUtils.readResourceFile("client/put-request-client-200.json");
        String expectedResponse = fileUtils.readResourceFile("client/put-response-not-match-ids-client-400.json");

        ClientPutRequest clientPutRequest = ClientUtils.newClientPutRequest();
        long randomId = 999L;

        BDDMockito.doThrow(new BadRequestException("The ID in the request body (%s) does not match the ID in the URL (%s)".formatted(clientPutRequest.getId(), randomId)))
                .when(service).update(ArgumentMatchers.anyLong(), ArgumentMatchers.any());

        mockMvc.perform(MockMvcRequestBuilders.put(URL + "/{id}", randomId)
                        .content(request)
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().json(expectedResponse));
    }

    @Test
    @DisplayName("PUT /v1/clients/1 throws NotFoundException() when the client is not found")
    @Order(15)
    void update_ThrowsNotFoundException_WhenTheClientIsNotFound() throws Exception {
        String request = fileUtils.readResourceFile("client/put-request-invalid-client-404.json");
        String expectedResponse = fileUtils.readResourceFile("client/put-response-invalid-client-404.json");

        long randomId = 999L;

        BDDMockito.doThrow(new NotFoundException("Client not Found"))
                .when(service).update(ArgumentMatchers.anyLong(), ArgumentMatchers.any());

        mockMvc.perform(MockMvcRequestBuilders.put(URL + "/{id}", randomId)
                        .content(request)
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect(MockMvcResultMatchers.content().json(expectedResponse));
    }

    @Test
    @DisplayName("PUT /v1/clients/1 throws BadRequestException when the email to update does not belong to client")
    @Order(16)
    void update_ThrowsBadRequestException_WhenTheEmailToUpdateDoesNotBelongToClient() throws Exception {
        String request = fileUtils.readResourceFile("client/put-request-client-email-existing-400.json");
        String expectedResponse = fileUtils.readResourceFile("client/put-response-client-email-existing-400.json");

        Client clientEmailOwner = ClientUtils.newClientList().get(1);

        ClientPutRequest clientPutRequest = ClientUtils.newClientPutRequest().withEmail(clientEmailOwner.getEmail());

        BDDMockito.doThrow(new BadRequestException("Email '%s' already exists".formatted(clientPutRequest.getEmail())))
                .when(service).update(ArgumentMatchers.anyLong(), ArgumentMatchers.any());

        mockMvc.perform(MockMvcRequestBuilders.put(URL + "/{id}", clientPutRequest.getId())
                        .content(request)
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().json(expectedResponse));
    }

    @Test
    @DisplayName("PUT /v1/clients/1 throws BadRequestException when the some phone to update does not belong to client")
    @Order(17)
    void update_ThrowsBadRequestException_WhenThePhoneToUpdateDoesNotBelongToClient() throws Exception {
        String request = fileUtils.readResourceFile("client/put-request-client-phone-existing-400.json");
        String expectedResponse = fileUtils.readResourceFile("client/put-response-client-phone-existing-400.json");

        Client clientPhoneOwner = ClientUtils.newClientList().get(1);

        ClientPutRequest clientPutRequest = ClientUtils.newClientPutRequest().withPhones(clientPhoneOwner.getPerson().getPhones());
        Phone phone = clientPutRequest.getPhones().getFirst();

        BDDMockito.doThrow(new BadRequestException("Phone '%s' already exists for another person".formatted(phone.getNumber())))
                .when(service).update(ArgumentMatchers.anyLong(), ArgumentMatchers.any());

        mockMvc.perform(MockMvcRequestBuilders.put(URL + "/{id}", clientPutRequest.getId())
                        .content(request)
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().json(expectedResponse));
    }

    @ParameterizedTest
    @MethodSource("putClientBadRequestSource")
    @DisplayName("PUT /v1/clients/1 return BadRequest when fields are invalid")
    @Order(18)
    void update_ReturnsBadRequest_WhenFieldAreInvalid(String fileName, List<String> expectedErrors) throws Exception {
        String request = fileUtils.readResourceFile("client/%s".formatted(fileName));

        MvcResult mvcResult = mockMvc.perform(
                        MockMvcRequestBuilders.put(URL + "/{id}", 1L)
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

    private static Stream<Arguments> putClientBadRequestSource() {
        String emailNotValidError = "Email is not valid";

        List<String> expectedRequiredErrors = allRequiredErrors();

        return Stream.of(
                Arguments.of("put-request-client-empty-fields-400.json", expectedRequiredErrors),
                Arguments.of("put-request-client-blank-fields-400.json", expectedRequiredErrors),
                Arguments.of("put-request-client-invalid-email-400.json", List.of(emailNotValidError))
        );
    }

    private static List<String> basicsRequiredErrors() {
        String nameRequiredError = "The field 'name' is required";
        String lastNameRequiredError = "The field 'lastName' is required";
        String emailRequiredError = "The field 'email' is required";

        return new ArrayList<>(List.of(nameRequiredError, lastNameRequiredError, emailRequiredError));
    }

    private static List<String> allRequiredErrors() {
        String addressRequiredError = "The field 'address' is required";
        String phonesRequiredError = "The field 'phones' is required";

        List<String> allRequiredErrors = basicsRequiredErrors();
        allRequiredErrors.addAll(List.of(addressRequiredError,phonesRequiredError));

        return allRequiredErrors;
    }

    @Test
    @DisplayName("DELETE /v1/clients/1 removes client when successful")
    @Order(19)
    void deleteById_RemovesClient_WhenSuccessful() throws Exception {
        Client clientToDelete = ClientUtils.newClientList().getFirst();
        Long idToDelete = clientToDelete.getId();

        BDDMockito.doNothing().when(service).deleteById(idToDelete);

        mockMvc.perform(MockMvcRequestBuilders.delete(URL + "/{id}", idToDelete))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isNoContent());
    }

    @Test
    @DisplayName("DELETE /v1/clients/25256595 throws NotFoundException when given id is not found")
    @Order(20)
    void deleteById_ThrowsNotFoundException_WhenGivenIdIsNotFound() throws Exception {
        Long randomId = 25256595L;

        BDDMockito.doThrow(new NotFoundException("Client not Found")).when(service).deleteById(randomId);

        String expectedResponse = fileUtils.readResourceFile("client/delete-client-by-id-404.json");

        mockMvc.perform(MockMvcRequestBuilders.delete(URL + "/{id}", randomId))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect(MockMvcResultMatchers.content().json(expectedResponse));
    }
}