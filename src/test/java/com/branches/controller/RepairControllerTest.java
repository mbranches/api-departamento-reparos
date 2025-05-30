package com.branches.controller;

import com.branches.exception.BadRequestException;
import com.branches.exception.NotFoundException;
import com.branches.model.Employee;
import com.branches.model.Piece;
import com.branches.model.Repair;
import com.branches.request.RepairEmployeePostRequest;
import com.branches.request.RepairPiecePostRequest;
import com.branches.request.RepairPostRequest;
import com.branches.response.RepairEmployeePostResponse;
import com.branches.response.RepairGetResponse;
import com.branches.response.RepairPiecePostResponse;
import com.branches.service.RepairEmployeeService;
import com.branches.service.RepairPieceService;
import com.branches.service.RepairService;
import com.branches.utils.*;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
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

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

@WebMvcTest(controllers = RepairController.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@Import(FileUtils.class)
class RepairControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @MockitoBean
    private RepairService service;
    @MockitoBean
    private RepairPieceService repairPieceService;
    @MockitoBean
    private RepairEmployeeService repairEmployeeService;
    @Autowired
    private FileUtils fileUtils;
    private List<RepairGetResponse> repairGetResponseList;
    private final String URL = "/v1/repairs";

    @BeforeEach
    void init() {
        repairGetResponseList = RepairUtils.newRepairGetResponseList();
    }

    @Test
    @DisplayName("GET /v1/repairs returns all repairs when argument is null")
    @Order(1)
    void findAll_ReturnsAllRepairs_WhenSuccessful() throws Exception {
        BDDMockito.when(service.findAll(null)).thenReturn(repairGetResponseList);

        String expectedResponse = fileUtils.readResourceFile("repair/get-repairs-null-repairDate-200.json");

        mockMvc.perform(MockMvcRequestBuilders.get(URL))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().json(expectedResponse));
    }

    @Test
    @DisplayName("GET /v1/repairs?dateRepair=2025-02-12 returns all repairs in date range when argument is given")
    @Order(2)
    void findAll_ReturnsAllRepairsInDateRange_WhenArgumentIsGiven() throws Exception {
        LocalDate dateToSearch = LocalDate.of(2025, 2, 12);
        String dateParam = dateToSearch.toString();

        BDDMockito.when(service.findAll(dateToSearch)).thenReturn(repairGetResponseList);

        String expectedResponse = fileUtils.readResourceFile("repair/get-repairs-valid-repairDate-200.json");

        mockMvc.perform(
                    MockMvcRequestBuilders.get(URL)
                            .param("dateRepair", dateParam)
                )
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().json(expectedResponse));
    }

    @Test
    @DisplayName("GET /v1/repairs?dateRepair=2026-12-15 returns an empty list when there are no repairs in date range")
    @Order(3)
    void findAll_ReturnsEmptyList_WhenThereAreNoRepairsInDateRange() throws Exception {
        LocalDate dateToSearch = LocalDate.of(2026, 12, 15);
        String dateParam = dateToSearch.toString();

        BDDMockito.when(service.findAll(dateToSearch)).thenReturn(Collections.emptyList());

        String expectedResponse = fileUtils.readResourceFile("repair/get-repairs-invalid-repairDate-200.json");

        mockMvc.perform(
                    MockMvcRequestBuilders.get(URL)
                            .param("dateRepair", dateParam)
                )
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().json(expectedResponse));
    }

    @Test
    @DisplayName("GET /v1/repairs/1 returns found repair when successful")
    @Order(4)
    void findById_ReturnsFoundRepair_WhenSuccessful() throws Exception {
        RepairGetResponse expectedRepair = repairGetResponseList.getFirst();
        long idToSearch = 1L;

        BDDMockito.when(service.findById(idToSearch)).thenReturn(expectedRepair);
        String expectedResponse = fileUtils.readResourceFile("repair/get-repair-by-id-200.json");

        mockMvc.perform(MockMvcRequestBuilders.get(URL + "/{id}", idToSearch))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().json(expectedResponse));
    }

    @Test
    @DisplayName("GET /v1/repairs/131222 throws NotFoundException when id is not found")
    @Order(5)
    void findById_ThrowsNotFoundException_WhenIdIsNotFound() throws Exception {
        long randomId = 131222L;

        BDDMockito.when(service.findById(randomId))

                .thenThrow(new NotFoundException("Repair with id '%s' not Found".formatted(randomId)));
        String expectedResponse = fileUtils.readResourceFile("repair/get-repair-by-id-404.json");

        mockMvc.perform(MockMvcRequestBuilders.get(URL + "/{id}", randomId))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect(MockMvcResultMatchers.content().json(expectedResponse));
    }

    @Test
    @DisplayName("GET /v1/repairs/1/employees returns all repair employees from given repair id when successful")
    @Order(6)
    void findEmployeesByRepairId_ReturnsAllRepairEmployeesFromGivenRepairId_WhenSuccessful() throws Exception {
        Repair repairToSearch = RepairUtils.newRepairList().getFirst();
        Long idToSearch = repairToSearch.getId();

        BDDMockito.when(repairEmployeeService.findAllByRepairId(idToSearch)).thenReturn(List.of(RepairEmployeeUtils.newRepairEmployeeByRepairGetEmployees()));

        String expectedResponse = fileUtils.readResourceFile("repair/get-employees-1-repairId-200.json");

        mockMvc.perform(MockMvcRequestBuilders.get(URL + "/{id}/employees", idToSearch))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().json(expectedResponse));
    }

    @Test
    @DisplayName("GET /v1/repairs/3/employees returns an empty list when when repair contain no employees")
    @Order(7)
    void findEmployeesByRepairId_ReturnsEmptyList_WhenRepairContainNoEmployees() throws Exception {
        Repair repairToSearch = RepairUtils.newRepairList().getLast();
        Long idToSearch = repairToSearch.getId();

        BDDMockito.when(repairEmployeeService.findAllByRepairId(idToSearch)).thenReturn(Collections.emptyList());

        String expectedResponse = fileUtils.readResourceFile("repair/get-employees-3-repairId-200.json");

        mockMvc.perform(MockMvcRequestBuilders.get(URL + "/{id}/employees", idToSearch))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().json(expectedResponse));
    }

    @Test
    @DisplayName("GET /v1/repairs/121123/employees throws NotFoundException when given id is not found")
    @Order(8)
    void findEmployeesByRepairId_ThrowsNotFoundException_WhenGivenIdIsNotFound() throws Exception {
        Long randomId = 121123L;

        BDDMockito.when(repairEmployeeService.findAllByRepairId(randomId))
                .thenThrow(new NotFoundException("Repair with id '%s' not Found".formatted(randomId)));

        String expectedResponse = fileUtils.readResourceFile("repair/get-employees-invalid-repairId-404.json");

        mockMvc.perform(MockMvcRequestBuilders.get(URL + "/{id}/employees", randomId))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect(MockMvcResultMatchers.content().json(expectedResponse));
    }

    @Test
    @DisplayName("GET /v1/repairs/1/pieces returns all repair pieces from given repair id when successful")
    @Order(9)
    void findPiecesByRepairId_ReturnsAllRepairPiecesFromGivenRepairId_WhenSuccessful() throws Exception {
        Repair repairToSearch = RepairUtils.newRepairList().getFirst();
        Long idToSearch = repairToSearch.getId();

        BDDMockito.when(repairPieceService.findAllByRepairId(idToSearch)).thenReturn(List.of(RepairPieceUtils.newRepairPieceByRepairGetPieces()));

        String expectedResponse = fileUtils.readResourceFile("repair/get-pieces-1-repairId-200.json");

        mockMvc.perform(MockMvcRequestBuilders.get(URL + "/{id}/pieces", idToSearch))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().json(expectedResponse));
    }

    @Test
    @DisplayName("GET /v1/repairs/3/pieces returns an empty list when when repair contain no pieces")
    @Order(10)
    void findPiecesByRepairId_ReturnsEmptyList_WhenRepairContainNoPieces() throws Exception {
        Repair repairToSearch = RepairUtils.newRepairList().getLast();
        Long idToSearch = repairToSearch.getId();

        BDDMockito.when(repairPieceService.findAllByRepairId(idToSearch)).thenReturn(Collections.emptyList());

        String expectedResponse = fileUtils.readResourceFile("repair/get-pieces-3-repairId-200.json");

        mockMvc.perform(MockMvcRequestBuilders.get(URL + "/{id}/pieces", idToSearch))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().json(expectedResponse));
    }

    @Test
    @DisplayName("GET /v1/repairs/121123/pieces throws NotFoundException when given id is not found")
    @Order(11)
    void findPiecesByRepairId_ThrowsNotFoundException_WhenGivenIdIsNotFound() throws Exception {
        Long randomId = 121123L;

        BDDMockito.when(repairPieceService.findAllByRepairId(randomId))
                .thenThrow(new NotFoundException("Repair with id '%s' not Found".formatted(randomId)));

        String expectedResponse = fileUtils.readResourceFile("repair/get-pieces-invalid-repairId-404.json");

        mockMvc.perform(MockMvcRequestBuilders.get(URL + "/{id}/pieces", randomId))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect(MockMvcResultMatchers.content().json(expectedResponse));
    }

    @Test
    @DisplayName("POST /v1/repairs returns saved repair when successful")
    @Order(12)
    void save_ReturnsSavedRepair_WhenSuccessful() throws Exception {
        String request = fileUtils.readResourceFile("repair/post-request-repair-200.json");
        String expectedResponse = fileUtils.readResourceFile("repair/post-response-repair-201.json");

        RepairPostRequest postRequest = RepairUtils.newRepairPostRequest();

        BDDMockito.when(service.save(postRequest)).thenReturn(RepairUtils.newRepairPostResponse());

        mockMvc.perform(
                    MockMvcRequestBuilders.post(URL)
                            .content(request)
                            .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.content().json(expectedResponse));
    }

    @Test
    @DisplayName("POST /v1/repairs throws NotFoundException when client is not found")
    @Order(13)
    void save_ThrowsNotFoundException_WhenClientIsNotFound() throws Exception {
        String request = fileUtils.readResourceFile("repair/post-request-repair-invalid-client-200.json");
        String expectedResponse = fileUtils.readResourceFile("repair/post-response-repair-invalid-client-404.json");

        long randomClientId = 4551L;
        RepairPostRequest postRequest = RepairUtils.newRepairPostRequest().withClientId(randomClientId);

        BDDMockito.when(service.save(postRequest))
                .thenThrow(new NotFoundException("Client with id '%s' not Found".formatted(randomClientId)));

        mockMvc.perform(
                        MockMvcRequestBuilders.post(URL)
                                .content(request)
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect(MockMvcResultMatchers.content().json(expectedResponse));
    }

    @Test
    @DisplayName("POST /v1/repairs throws NotFoundException when vehicle is not found")
    @Order(14)
    void save_ThrowsNotFoundException_WhenVehicleIsNotFound() throws Exception {
        String request = fileUtils.readResourceFile("repair/post-request-repair-invalid-vehicle-200.json");
        String expectedResponse = fileUtils.readResourceFile("repair/post-response-repair-invalid-vehicle-404.json");

        long randomVehicleId = 455L;
        RepairPostRequest postRequest = RepairUtils.newRepairPostRequest().withVehicleId(randomVehicleId);

        BDDMockito.when(service.save(postRequest))
                .thenThrow(new NotFoundException("Vehicle with id '%s' not Found".formatted(randomVehicleId)));

        mockMvc.perform(
                        MockMvcRequestBuilders.post(URL)
                                .content(request)
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect(MockMvcResultMatchers.content().json(expectedResponse));
    }
    
    @ParameterizedTest
    @MethodSource("postRepairBadRequestSource")
    @DisplayName("POST /v1/repairs throws BadRequestException when fields are invalid")
    @Order(15)
    void save_ThrowsBadRequestException_WhenFieldAreInvalid(String fileName, List<String> expectedErrors) throws Exception {
        String request = fileUtils.readResourceFile("repair/%s".formatted(fileName));

        MvcResult mvcResult = mockMvc.perform(
                        MockMvcRequestBuilders.post(URL)
                                .content(request)
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andReturn();

        Exception exception = mvcResult.getResolvedException();

        assert exception != null;
        Assertions.assertThat(exception.getMessage())
                .isNotNull()
                .contains(expectedErrors);
    }

    private static Stream<Arguments> postRepairBadRequestSource() {
        String clientIdNotNull = "The field 'clientId' cannot be null";
        String vehicleIdNotNull = "The field 'vehicleId' cannot be null";

        List<String> expectedErrors = List.of(clientIdNotNull, vehicleIdNotNull);

        return Stream.of(
                Arguments.of("post-request-repair-null-fields-400.json", expectedErrors)
        );
    }

    @Test
    @DisplayName("POST /v1/repairs/1/employees return saved RepairEmployee when successful")
    @Order(16)
    void addEmployee_ReturnsSavedRepairEmployee_WhenSuccessful() throws Exception {
        Repair repair = RepairUtils.newRepairList().getFirst();
        Long repairId = repair.getId();

        String request = fileUtils.readResourceFile("repair/post-request-repairEmployee-200.json");
        String expectedResponse = fileUtils.readResourceFile("repair/post-response-repairEmployee-201.json");

        RepairEmployeePostRequest postRequest = RepairEmployeeUtils.newRepairEmployeePostRequest();
        RepairEmployeePostResponse postResponse = RepairEmployeeUtils.newRepairEmployeePostResponse();

        BDDMockito.when(repairEmployeeService.save(repairId, postRequest))
                        .thenReturn(postResponse);

        mockMvc.perform(MockMvcRequestBuilders.post(
                URL + "/" + repairId + "/employees")
                                .content(request)
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.content().json(expectedResponse));
    }

    @Test
    @DisplayName("POST /v1/repairs/77127/employees throws NotFoundException when repairId is not found")
    @Order(17)
    void addEmployee_ThrowsNotFoundException_WhenRepairIdIsNotFound() throws Exception {
        Long randomRepairId = 77127L;

        String request = fileUtils.readResourceFile("repair/post-request-repairEmployee-200.json");
        String expectedResponse = fileUtils.readResourceFile("repair/post-response-repairEmployee-invalid-repair-404.json");

        RepairEmployeePostRequest postRequest = RepairEmployeeUtils.newRepairEmployeePostRequest();
        BDDMockito.when(repairEmployeeService.save(randomRepairId, postRequest))
                .thenThrow(new NotFoundException("Repair with id '%s' not Found".formatted(randomRepairId)));

        mockMvc.perform(MockMvcRequestBuilders.post(
                                URL + "/" + randomRepairId + "/employees")
                        .content(request)
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect(MockMvcResultMatchers.content().json(expectedResponse));
    }

    @Test
    @DisplayName("POST /v1/repairs/1/employees throws BadRequestException when some given employee is not found")
    @Order(18)
    void addEmployee_ThrowsBadRequestException_WhenSomeGivenEmployeeIsNotFound() throws Exception {
        String request = fileUtils.readResourceFile("repair/post-request-repairEmployee-invalid-employee-200.json");
        String expectedResponse = fileUtils.readResourceFile("repair/post-response-repairEmployee-invalid-employee-400.json");

        Repair repair = RepairUtils.newRepairList().getFirst();
        Long repairId = repair.getId();

        Long randomEmployeeId = 4554444L;
        RepairEmployeePostRequest postRequest = RepairEmployeeUtils.newRepairEmployeePostRequest().withEmployeeId(randomEmployeeId);

        BDDMockito.when(repairEmployeeService.save(repairId, postRequest))
                .thenThrow(new NotFoundException("Employee with id '%s' not Found".formatted(randomEmployeeId)));

        mockMvc.perform(MockMvcRequestBuilders.post(
                                URL + "/" + repairId + "/employees")
                        .content(request)
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect(MockMvcResultMatchers.content().json(expectedResponse));
    }

    @ParameterizedTest
    @MethodSource("postRepairEmployeeBadRequestSource")
    @DisplayName("POST /v1/repairs/1/employees throws BadRequestException when fields are invalid")
    @Order(19)
    void addEmployee_ThrowsBadRequestException_WhenFieldAreInvalid(String fileName, List<String> expectedErrors) throws Exception {
        String request = fileUtils.readResourceFile("repair/%s".formatted(fileName));

        long id = 1L;

        MvcResult mvcResult = mockMvc.perform(
                        MockMvcRequestBuilders.post(URL + "/" + id + "/employees")
                                .content(request)
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andReturn();

        Exception exception = mvcResult.getResolvedException();

        assert exception != null;
        Assertions.assertThat(exception.getMessage())
                .isNotNull()
                .contains(expectedErrors);
    }

    public static Stream<Arguments> postRepairEmployeeBadRequestSource() {
        String nullEmployeeIdErrorMessage = "The field 'employeeId' cannot be null";
        String nullHoursWorkedErrorMessage = "The field 'hoursWorked' cannot be null";

        String negativeHoursWorkedErrorMessage = "'hoursWorked' must be equal to or greater than 0";

        List<String> nullFieldsErrors = List.of(nullEmployeeIdErrorMessage, nullHoursWorkedErrorMessage);

        return Stream.of(
                Arguments.of("post-request-repairEmployee-null-fields-400.json", nullFieldsErrors),
                Arguments.of("post-request-repairEmployee-negative-hoursWorked-400.json", Collections.singletonList(negativeHoursWorkedErrorMessage))
        );
    }

    @Test
    @DisplayName("POST /v1/repairs/1/pieces returns saved RepairPieces when successful")
    @Order(20)
    void addPiece_ReturnsSavedRepairPiece_WhenSuccessful() throws Exception {
        Repair repair = RepairUtils.newRepairList().getFirst();
        Long repairId = repair.getId();

        String request = fileUtils.readResourceFile("repair/post-request-repairPiece-200.json");
        String expectedResponse = fileUtils.readResourceFile("repair/post-response-repairPiece-201.json");

        RepairPiecePostRequest postRequest = RepairPieceUtils.newRepairPiecePostRequest();
        RepairPiecePostResponse postResponse = RepairPieceUtils.newRepairPiecePostResponse();

        BDDMockito.when(repairPieceService.save(repairId, postRequest))
                .thenReturn(postResponse);

        mockMvc.perform(MockMvcRequestBuilders.post(
                                URL + "/" + repairId + "/pieces")
                        .content(request)
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.content().json(expectedResponse));
    }

    @Test
    @DisplayName("POST /v1/repairs/77127/pieces throws NotFoundException when repairId is not found")
    @Order(21)
    void addPiece_ThrowsNotFoundException_WhenRepairIdIsNotFound() throws Exception {
        Long randomRepairId = 77127L;

        String request = fileUtils.readResourceFile("repair/post-request-repairPiece-200.json");
        String expectedResponse = fileUtils.readResourceFile("repair/post-response-repairPiece-invalid-repair-404.json");

        RepairPiecePostRequest postRequest = RepairPieceUtils.newRepairPiecePostRequest();
        BDDMockito.when(repairPieceService.save(randomRepairId, postRequest))
                .thenThrow(new NotFoundException("Repair with id '%s' not Found".formatted(randomRepairId)));

        mockMvc.perform(MockMvcRequestBuilders.post(
                                URL + "/" + randomRepairId + "/pieces")
                        .content(request)
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect(MockMvcResultMatchers.content().json(expectedResponse));
    }

    @Test
    @DisplayName("POST /v1/repairs/1/pieces throws BadRequestException when some given piece is not found")
    @Order(22)
    void addPiece_ThrowsBadRequestException_WhenSomeGivenPieceIsNotFound() throws Exception {
        String request = fileUtils.readResourceFile("repair/post-request-repairPiece-invalid-piece-200.json");
        String expectedResponse = fileUtils.readResourceFile("repair/post-response-repairPiece-invalid-piece-400.json");

        Repair repair = RepairUtils.newRepairList().getFirst();
        Long repairId = repair.getId();

        long randomPieceId = 4554444L;
        RepairPiecePostRequest postRequest = RepairPieceUtils.newRepairPiecePostRequest().withPieceId(randomPieceId);

        BDDMockito.when(repairPieceService.save(repairId, postRequest))
                .thenThrow(new NotFoundException("Piece with id '%s' not Found".formatted(randomPieceId)));

        mockMvc.perform(MockMvcRequestBuilders.post(
                                URL + "/" + repairId + "/pieces")
                        .content(request)
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect(MockMvcResultMatchers.content().json(expectedResponse));
    }

    @Test
    @DisplayName("POST /v1/repairs/1/pieces throws BadRequestException when the piece has insufficient stock")
    @Order(23)
    void addPiece_ThrowsBadRequestException_WhenThePieceHasInsufficientStock() throws Exception {
        String request = fileUtils.readResourceFile("repair/post-request-repairPiece-invalid-quantity-200.json");
        String expectedResponse = fileUtils.readResourceFile("repair/post-response-repairPiece-invalid-quantity-400.json");

        Repair repair = RepairUtils.newRepairList().getFirst();
        Long repairId = repair.getId();

        Piece piece = PieceUtils.newPieceList().getFirst();

        RepairPiecePostRequest postRequest = RepairPieceUtils.newRepairPiecePostRequest().withQuantity(60);

        BDDMockito.when(repairPieceService.save(repairId, postRequest))
                .thenThrow(new BadRequestException("'" + piece.getName() + "' has insufficient stock." +
                        " Available: " + piece.getStock() + ", Requested: 60"));

        mockMvc.perform(MockMvcRequestBuilders.post(
                                URL + "/" + repairId + "/pieces")
                        .content(request)
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().json(expectedResponse));
    }

    @ParameterizedTest
    @MethodSource("postRepairPieceBadRequestSource")
    @DisplayName("POST /v1/repairs/1/pieces throws BadRequestException when fields are invalid")
    @Order(24)
    void addPiece_ThrowsBadRequestException_WhenFieldAreInvalid(String fileName, List<String> expectedErrors) throws Exception {
        String request = fileUtils.readResourceFile("repair/%s".formatted(fileName));

        long id = 1L;

        MvcResult mvcResult = mockMvc.perform(
                        MockMvcRequestBuilders.post(URL + "/" + id + "/pieces")
                                .content(request)
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andReturn();

        Exception exception = mvcResult.getResolvedException();

        assert exception != null;
        Assertions.assertThat(exception.getMessage())
                .isNotNull()
                .contains(expectedErrors);
    }

    public static Stream<Arguments> postRepairPieceBadRequestSource() {
        String nullPieceIdErrorMessage = "The field 'pieceId' cannot be null";
        String nullQuantityErrorMessage = "The field 'quantity' cannot be null";

        String negativeQuantityError = "'quantity' must be equal to or greater than 0";

        List<String> nullFieldsErrors = List.of(nullPieceIdErrorMessage, nullQuantityErrorMessage);

        return Stream.of(
                Arguments.of("post-request-repairPiece-null-fields-400.json", nullFieldsErrors),
                Arguments.of("post-request-repairPiece-negative-quantity-400.json", Collections.singletonList(negativeQuantityError))
        );
    }

    @Test
    @DisplayName("DELETE /v1/repairs/1 removes repair when successful")
    @Order(25)
    void deleteById_RemovesRepair_WhenSuccessful() throws Exception {
        Repair repairToDelete = RepairUtils.newRepairList().getFirst();
        Long idToDelete = repairToDelete.getId();

        BDDMockito.doNothing().when(service).deleteById(idToDelete);

        mockMvc.perform(MockMvcRequestBuilders.delete(URL + "/{id}", idToDelete))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isNoContent());
    }

    @Test
    @DisplayName("DELETE /v1/repairs/25256595 throws NotFoundException when given id is not found")
    @Order(26)
    void deleteById_ThrowsNotFoundException_WhenGivenIdIsNotFound() throws Exception {
        Long randomId = 25256595L;

        BDDMockito.doThrow(new NotFoundException("Repair with id '%s' not Found".formatted(randomId))).when(service).deleteById(randomId);

        String expectedResponse = fileUtils.readResourceFile("repair/delete-repair-by-id-404.json");

        mockMvc.perform(MockMvcRequestBuilders.delete(URL + "/{id}", randomId))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect(MockMvcResultMatchers.content().json(expectedResponse));
    }

    @Test
    @DisplayName("DELETE /v1/repairs/1/employees/1 removes employee from repair when successful")
    @Order(27)
    void removesRepairEmployeeById_RemovesEmployeeFromRepair_WhenSuccessful() throws Exception {
        Repair repair = RepairUtils.newRepairList().getFirst();
        Long repairId = repair.getId();
        Employee employee = EmployeeUtils.newEmployeeList().getFirst();
        Long employeeId = employee.getId();

        BDDMockito.doNothing().when(repairEmployeeService).deleteByRepairIdAndEmployeeId(repairId, employeeId);

        mockMvc.perform(MockMvcRequestBuilders.delete(URL + "/{repairId}/employees/{employeeId}", repairId, employeeId))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isNoContent());
    }

    @Test
    @DisplayName("DELETE /v1/repairs/25256595/employees/1 throws NotFoundException when repair is not found")
    @Order(28)
    void removesRepairEmployeeById_ThrowsNotFoundException_WhenRepairIsNotFound() throws Exception {
        Long randomRepairId = 25256595L;
        Employee employee = EmployeeUtils.newEmployeeList().getFirst();
        Long employeeId = employee.getId();

        BDDMockito.doThrow(new NotFoundException("Repair with id '%s' not Found".formatted(randomRepairId))).when(repairEmployeeService).deleteByRepairIdAndEmployeeId(randomRepairId, employeeId);

        String expectedResponse = fileUtils.readResourceFile("repair/delete-repairEmployee-invalid-repair-404.json");

        mockMvc.perform(MockMvcRequestBuilders.delete(URL + "/{repairId}/employees/{employeeId}", randomRepairId, employeeId))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect(MockMvcResultMatchers.content().json(expectedResponse));
    }

    @Test
    @DisplayName("DELETE /v1/repairs/1/employees/25256595 throws NotFoundException when employee is not found")
    @Order(29)
    void removesRepairEmployeeById_ThrowsNotFoundException_WhenEmployeeIsNotFound() throws Exception {
        Repair repair = RepairUtils.newRepairList().getFirst();
        Long repairId = repair.getId();
        Long randomEmployeeId = 25256595L;

        BDDMockito.doThrow(new NotFoundException("Employee with id '%s' not Found".formatted(randomEmployeeId))).when(repairEmployeeService).deleteByRepairIdAndEmployeeId(repairId, randomEmployeeId);
        String expectedResponse = fileUtils.readResourceFile("repair/delete-repairEmployee-invalid-employee-404.json");

        mockMvc.perform(MockMvcRequestBuilders.delete(URL + "/{repairId}/employees/{employeeId}", repairId, randomEmployeeId))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect(MockMvcResultMatchers.content().json(expectedResponse));
    }

    @Test
    @DisplayName("DELETE /v1/repairs/1/employees/3 throws NotFoundException when employee is not found in the repair")
    @Order(30)
    void removesRepairEmployeeById_ThrowsNotFoundException_WhenEmployeeIsNotFoundInTheRepair() throws Exception {
        Repair repair = RepairUtils.newRepairList().getFirst();
        Long repairId = repair.getId();
        Employee employee = EmployeeUtils.newEmployeeList().getLast();
        Long employeeId = employee.getId();

        BDDMockito.doThrow(new NotFoundException("The employee was not found in the repair")).when(repairEmployeeService).deleteByRepairIdAndEmployeeId(repairId, employeeId);

        String expectedResponse = fileUtils.readResourceFile("repair/delete-repairEmployee-invalid-repairEmployee-404.json");

        mockMvc.perform(MockMvcRequestBuilders.delete(URL + "/{repairId}/employees/{employeeId}", repairId, employeeId))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect(MockMvcResultMatchers.content().json(expectedResponse));
    }

    @Test
    @DisplayName("DELETE /v1/repairs/1/pieces/1 removes piece from repair when successful")
    @Order(31)
    void removesRepairPieceById_RemovesPieceFromRepair_WhenSuccessful() throws Exception {
        Repair repair = RepairUtils.newRepairList().getFirst();
        Long repairId = repair.getId();
        Piece piece = PieceUtils.newPieceList().getFirst();
        Long pieceId = piece.getId();

        BDDMockito.doNothing().when(repairPieceService).deleteByRepairIdAndPieceId(repairId, pieceId);

        mockMvc.perform(MockMvcRequestBuilders.delete(URL + "/{repairId}/pieces/{pieceId}", repairId, pieceId))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isNoContent());
    }

    @Test
    @DisplayName("DELETE /v1/repairs/25256595/pieces/1 throws NotFoundException when repair is not found")
    @Order(32)
    void removesRepairPieceById_ThrowsNotFoundException_WhenRepairIsNotFound() throws Exception {
        Long randomRepairId = 25256595L;
        Piece piece = PieceUtils.newPieceList().getFirst();
        Long pieceId = piece.getId();

        BDDMockito.doThrow(new NotFoundException("Repair with id '%s' not Found".formatted(randomRepairId))).when(repairPieceService).deleteByRepairIdAndPieceId(randomRepairId, pieceId);

        String expectedResponse = fileUtils.readResourceFile("repair/delete-repairPiece-invalid-repair-404.json");

        mockMvc.perform(MockMvcRequestBuilders.delete(URL + "/{repairId}/pieces/{pieceId}", randomRepairId, pieceId))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect(MockMvcResultMatchers.content().json(expectedResponse));
    }

    @Test
    @DisplayName("DELETE /v1/repairs/1/pieces/25256595 throws NotFoundException when piece is not found")
    @Order(33)
    void removesRepairPieceById_ThrowsNotFoundException_WhenPieceIsNotFound() throws Exception {
        Repair repair = RepairUtils.newRepairList().getFirst();
        Long repairId = repair.getId();
        Long randomPieceId = 25256595L;

        BDDMockito.doThrow(new NotFoundException("Piece with id '%s' not Found".formatted(randomPieceId))).when(repairPieceService).deleteByRepairIdAndPieceId(repairId, randomPieceId);

        String expectedResponse = fileUtils.readResourceFile("repair/delete-repairPiece-invalid-piece-404.json");

        mockMvc.perform(MockMvcRequestBuilders.delete(URL + "/{repairId}/pieces/{pieceId}", repairId, randomPieceId))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect(MockMvcResultMatchers.content().json(expectedResponse));
    }

    @Test
    @DisplayName("DELETE /v1/repairs/1/pieces/3 throws NotFoundException when piece is not found in the repair")
    @Order(34)
    void removesRepairPieceById_ThrowsNotFoundException_WhenPieceIsNotFoundInTheRepair() throws Exception {
        Repair repair = RepairUtils.newRepairList().getFirst();
        Long repairId = repair.getId();
        Piece piece = PieceUtils.newPieceList().getLast();
        Long pieceId = piece.getId();

        BDDMockito.doThrow(new NotFoundException("The piece was not found in the repair")).when(repairPieceService).deleteByRepairIdAndPieceId(repairId, pieceId);

        String expectedResponse = fileUtils.readResourceFile("repair/delete-repairPiece-invalid-repairPiece-404.json");

        mockMvc.perform(MockMvcRequestBuilders.delete(URL + "/{repairId}/pieces/{pieceId}", repairId, pieceId))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect(MockMvcResultMatchers.content().json(expectedResponse));
    }
}