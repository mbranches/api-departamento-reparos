package com.branches.controller;

import com.branches.exception.BadRequestException;
import com.branches.exception.NotFoundException;
import com.branches.model.Employee;
import com.branches.model.Piece;
import com.branches.model.Repair;
import com.branches.request.RepairPostRequest;
import com.branches.response.RepairGetResponse;
import com.branches.service.RepairService;
import com.branches.utils.*;
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

        BDDMockito.when(service.findById(randomId)).thenThrow(new NotFoundException("Repair not Found"));
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

        BDDMockito.when(service.findEmployeesByRepairId(idToSearch)).thenReturn(List.of(RepairEmployeeUtils.newRepairEmployeeByRepairGetEmployees()));

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

        BDDMockito.when(service.findEmployeesByRepairId(idToSearch)).thenReturn(Collections.emptyList());

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

        BDDMockito.when(service.findEmployeesByRepairId(randomId)).thenThrow(new NotFoundException("Repair not Found"));

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

        BDDMockito.when(service.findPiecesByRepairId(idToSearch)).thenReturn(List.of(RepairPieceUtils.newRepairPieceByRepairGetPieces()));

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

        BDDMockito.when(service.findPiecesByRepairId(idToSearch)).thenReturn(Collections.emptyList());

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

        BDDMockito.when(service.findPiecesByRepairId(randomId)).thenThrow(new NotFoundException("Repair not Found"));

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

        BDDMockito.when(service.save(ArgumentMatchers.any(RepairPostRequest.class))).thenReturn(RepairUtils.newRepairPostResponse());

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

        BDDMockito.when(service.save(ArgumentMatchers.any(RepairPostRequest.class))).thenThrow(new NotFoundException("Client not Found"));

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

        BDDMockito.when(service.save(ArgumentMatchers.any(RepairPostRequest.class))).thenThrow(new NotFoundException("Vehicle not Found"));

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
    @DisplayName("POST /v1/repairs throws BadRequestException when some piece is not found")
    @Order(15)
    void save_ThrowsBadRequestException_WhenSomePieceIsNotFound() throws Exception {
        String request = fileUtils.readResourceFile("repair/post-request-repair-invalid-piece-200.json");
        String expectedResponse = fileUtils.readResourceFile("repair/post-response-repair-invalid-piece-400.json");

        BDDMockito.when(service.save(ArgumentMatchers.any(RepairPostRequest.class))).thenThrow(new BadRequestException("Error saving pieces"));

        mockMvc.perform(
                        MockMvcRequestBuilders.post(URL)
                                .content(request)
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().json(expectedResponse));
    }

    @Test
    @DisplayName("POST /v1/repairs throws BadRequestException when some employee is not found")
    @Order(16)
    void save_ThrowsBadRequestException_WhenSomeEmployeeIsNotFound() throws Exception {
        String request = fileUtils.readResourceFile("repair/post-request-repair-invalid-employee-200.json");
        String expectedResponse = fileUtils.readResourceFile("repair/post-response-repair-invalid-employee-400.json");

        BDDMockito.when(service.save(ArgumentMatchers.any(RepairPostRequest.class))).thenThrow(new BadRequestException("Error saving employees"));

        mockMvc.perform(
                        MockMvcRequestBuilders.post(URL)
                                .content(request)
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().json(expectedResponse));
    }

    @Test
    @DisplayName("POST /v1/repairs throws BadRequestException when quantity is greater than piece stock")
    @Order(17)
    void save_ThrowsBadRequestException_WhenQuantityIsGreaterThanPieceStock() throws Exception {
        Piece pieceToSave = PieceUtils.newPieceToSave();

        String request = fileUtils.readResourceFile("repair/post-request-repair-invalid-piece-quantity-200.json");
        String expectedResponse = fileUtils.readResourceFile("repair/post-response-repair-invalid-piece-quantity-400.json");

        BDDMockito.when(service.save(ArgumentMatchers.any(RepairPostRequest.class))).thenThrow(new BadRequestException("'" + pieceToSave.getName() + "' has insufficient stock." +
                " Available: " + pieceToSave.getStock() + ", Requested: " + 5000));

        mockMvc.perform(
                        MockMvcRequestBuilders.post(URL)
                                .content(request)
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().json(expectedResponse));
    }
    
    @ParameterizedTest
    @MethodSource("postRepairBadRequestSource")
    @DisplayName("POST /v1/repairs throws BadRequestException when fields are invalid")
    @Order(18)
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
    @DisplayName("POST /v1/repairs/1/employees returns all saved RepairEmployees when successful")
    @Order(19)
    void addEmployee_ReturnsAllSavedRepairEmployees_WhenSuccessful() throws Exception {
        Repair repair = RepairUtils.newRepairList().getFirst();
        Long repairId = repair.getId();

        String request = fileUtils.readResourceFile("repair/post-request-repairEmployee-200.json");
        String expectedResponse = fileUtils.readResourceFile("repair/post-response-repairEmployee-201.json");

        BDDMockito.when(service.addEmployee(repairId, List.of(RepairEmployeeUtils.newRepairEmployeePostRequest())))
                        .thenReturn(List.of(RepairEmployeeUtils.newRepairEmployeeByRepairByAddEmployee()));

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
    @Order(20)
    void addEmployee_ThrowsNotFoundException_WhenRepairIdIsNotFound() throws Exception {
        Long randomRepairId = 77127L;

        String request = fileUtils.readResourceFile("repair/post-request-repairEmployee-200.json");
        String expectedResponse = fileUtils.readResourceFile("repair/post-response-repairEmployee-invalid-repair-404.json");

        BDDMockito.when(service.addEmployee(randomRepairId, List.of(RepairEmployeeUtils.newRepairEmployeePostRequest())))
                .thenThrow(new NotFoundException("Repair not Found"));

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
    @Order(21)
    void addEmployee_ThrowsBadRequestException_WhenSomeGivenEmployeeIsNotFound() throws Exception {
        Repair repair = RepairUtils.newRepairList().getFirst();
        Long repairId = repair.getId();

        String request = fileUtils.readResourceFile("repair/post-request-repairEmployee-invalid-employee-200.json");
        String expectedResponse = fileUtils.readResourceFile("repair/post-response-repairEmployee-invalid-employee-400.json");

        BDDMockito.when(service.addEmployee(ArgumentMatchers.anyLong(), ArgumentMatchers.anyList()))
                .thenThrow(new BadRequestException("Error saving employees"));

        mockMvc.perform(MockMvcRequestBuilders.post(
                                URL + "/" + repairId + "/employees")
                        .content(request)
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().json(expectedResponse));
    }

    @ParameterizedTest
    @MethodSource("postRepairEmployeeBadRequestSource")
    @DisplayName("POST /v1/repairs/1/employees throws BadRequestException when fields are invalid")
    @Order(22)
    void addEmployee_ThrowsBadRequestException_WhenFieldAreInvalid(String fileName) throws Exception {
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
                .contains("400");
    }

    public static Stream<Arguments> postRepairEmployeeBadRequestSource() {
        return Stream.of(
                Arguments.of("post-request-repairEmployee-null-employeeId-400.json"),
                Arguments.of("post-request-repairEmployee-null-hoursWorked-400.json"),
                Arguments.of("post-request-repairEmployee-negative-hoursWorked-400.json")
        );
    }

    @Test
    @DisplayName("POST /v1/repairs/1/pieces returns all saved RepairPieces when successful")
    @Order(23)
    void addPiece_ReturnsAllSavedRepairPieces_WhenSuccessful() throws Exception {
        Repair repair = RepairUtils.newRepairList().getFirst();
        Long repairId = repair.getId();

        String request = fileUtils.readResourceFile("repair/post-request-repairPiece-200.json");
        String expectedResponse = fileUtils.readResourceFile("repair/post-response-repairPiece-201.json");

        BDDMockito.when(service.addPiece(repairId, List.of(RepairPieceUtils.newRepairPiecePostRequest())))
                .thenReturn(List.of(RepairPieceUtils.newRepairPieceByRepairPostResponse()));

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
    @Order(24)
    void addPiece_ThrowsNotFoundException_WhenRepairIdIsNotFound() throws Exception {
        Long randomRepairId = 77127L;

        String request = fileUtils.readResourceFile("repair/post-request-repairPiece-200.json");
        String expectedResponse = fileUtils.readResourceFile("repair/post-response-repairPiece-invalid-repair-404.json");

        BDDMockito.when(service.addPiece(randomRepairId, List.of(RepairPieceUtils.newRepairPiecePostRequest())))
                .thenThrow(new NotFoundException("Repair not Found"));

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
    @Order(25)
    void addPiece_ThrowsBadRequestException_WhenSomeGivenPieceIsNotFound() throws Exception {
        Repair repair = RepairUtils.newRepairList().getFirst();
        Long repairId = repair.getId();

        String request = fileUtils.readResourceFile("repair/post-request-repairPiece-invalid-piece-200.json");
        String expectedResponse = fileUtils.readResourceFile("repair/post-response-repairPiece-invalid-piece-400.json");

        BDDMockito.when(service.addPiece(ArgumentMatchers.anyLong(), ArgumentMatchers.anyList()))
                .thenThrow(new BadRequestException("Error saving pieces"));

        mockMvc.perform(MockMvcRequestBuilders.post(
                                URL + "/" + repairId + "/pieces")
                        .content(request)
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().json(expectedResponse));
    }

    @Test
    @DisplayName("POST /v1/repairs/1/pieces throws BadRequestException when the piece has insufficient stock")
    @Order(26)
    void addPiece_ThrowsBadRequestException_WhenThePieceHasInsufficientStock() throws Exception {
        Repair repair = RepairUtils.newRepairList().getFirst();
        Long repairId = repair.getId();

        Piece piece = PieceUtils.newPieceToSave();

        String request = fileUtils.readResourceFile("repair/post-request-repairPiece-invalid-quantity-200.json");
        String expectedResponse = fileUtils.readResourceFile("repair/post-response-repairPiece-invalid-quantity-400.json");

        BDDMockito.when(service.addPiece(ArgumentMatchers.any(), ArgumentMatchers.anyList()))
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
    @Order(27)
    void addPiece_ThrowsBadRequestException_WhenFieldAreInvalid(String fileName) throws Exception {
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
                .contains("400");
    }

    public static Stream<Arguments> postRepairPieceBadRequestSource() {
        return Stream.of(
                Arguments.of("post-request-repairPiece-null-pieceId-400.json"),
                Arguments.of("post-request-repairPiece-null-quantity-400.json"),
                Arguments.of("post-request-repairPiece-negative-quantity-400.json")
        );
    }

    @Test
    @DisplayName("DELETE /v1/repairs/1 removes repair when successful")
    @Order(28)
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
    @Order(29)
    void deleteById_ThrowsNotFoundException_WhenGivenIdIsNotFound() throws Exception {
        Long randomId = 25256595L;

        BDDMockito.doThrow(new NotFoundException("Repair not Found")).when(service).deleteById(randomId);

        String expectedResponse = fileUtils.readResourceFile("repair/delete-repair-by-id-404.json");

        mockMvc.perform(MockMvcRequestBuilders.delete(URL + "/{id}", randomId))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect(MockMvcResultMatchers.content().json(expectedResponse));
    }

    @Test
    @DisplayName("DELETE /v1/repairs/1/employees/1 removes employee from repair when successful")
    @Order(30)
    void removesRepairEmployeeById_RemovesEmployeeFromRepair_WhenSuccessful() throws Exception {
        Repair repair = RepairUtils.newRepairList().getFirst();
        Long repairId = repair.getId();
        Employee employee = EmployeeUtils.newEmployeeList().getFirst();
        Long employeeId = employee.getId();

        BDDMockito.doNothing().when(service).removesRepairEmployeeById(repairId, employeeId);

        mockMvc.perform(MockMvcRequestBuilders.delete(URL + "/{repairId}/employees/{employeeId}", repairId, employeeId))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isNoContent());
    }

    @Test
    @DisplayName("DELETE /v1/repairs/25256595/employees/1 throws NotFoundException when repair is not found")
    @Order(31)
    void removesRepairEmployeeById_ThrowsNotFoundException_WhenRepairIsNotFound() throws Exception {
        Long randomRepairId = 25256595L;
        Employee employee = EmployeeUtils.newEmployeeList().getFirst();
        Long employeeId = employee.getId();

        BDDMockito.doThrow(new NotFoundException("Repair not Found")).when(service).removesRepairEmployeeById(randomRepairId, employeeId);

        String expectedResponse = fileUtils.readResourceFile("repair/delete-repairEmployee-invalid-repair-404.json");

        mockMvc.perform(MockMvcRequestBuilders.delete(URL + "/{repairId}/employees/{employeeId}", randomRepairId, employeeId))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect(MockMvcResultMatchers.content().json(expectedResponse));
    }

    @Test
    @DisplayName("DELETE /v1/repairs/1/employees/25256595 throws NotFoundException when employee is not found")
    @Order(32)
    void removesRepairEmployeeById_ThrowsNotFoundException_WhenEmployeeIsNotFound() throws Exception {
        Repair repair = RepairUtils.newRepairList().getFirst();
        Long repairId = repair.getId();
        Long randomEmployeeId = 25256595L;

        BDDMockito.doThrow(new NotFoundException("Employee not Found")).when(service).removesRepairEmployeeById(repairId, randomEmployeeId);

        String expectedResponse = fileUtils.readResourceFile("repair/delete-repairEmployee-invalid-employee-404.json");

        mockMvc.perform(MockMvcRequestBuilders.delete(URL + "/{repairId}/employees/{employeeId}", repairId, randomEmployeeId))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect(MockMvcResultMatchers.content().json(expectedResponse));
    }

    @Test
    @DisplayName("DELETE /v1/repairs/1/employees/3 throws NotFoundException when employee is not found in the repair")
    @Order(33)
    void removesRepairEmployeeById_ThrowsNotFoundException_WhenEmployeeIsNotFoundInTheRepair() throws Exception {
        Repair repair = RepairUtils.newRepairList().getFirst();
        Long repairId = repair.getId();
        Employee employee = EmployeeUtils.newEmployeeList().getLast();
        Long employeeId = employee.getId();

        BDDMockito.doThrow(new NotFoundException("The employee was not found in the repair")).when(service).removesRepairEmployeeById(repairId, employeeId);

        String expectedResponse = fileUtils.readResourceFile("repair/delete-repairEmployee-invalid-repairEmployee-404.json");

        mockMvc.perform(MockMvcRequestBuilders.delete(URL + "/{repairId}/employees/{employeeId}", repairId, employeeId))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect(MockMvcResultMatchers.content().json(expectedResponse));
    }

    @Test
    @DisplayName("DELETE /v1/repairs/1/pieces/1 removes piece from repair when successful")
    @Order(34)
    void removesRepairPieceById_RemovesPieceFromRepair_WhenSuccessful() throws Exception {
        Repair repair = RepairUtils.newRepairList().getFirst();
        Long repairId = repair.getId();
        Piece piece = PieceUtils.newPieceList().getFirst();
        Long pieceId = piece.getId();

        BDDMockito.doNothing().when(service).removesRepairPieceById(repairId, pieceId);

        mockMvc.perform(MockMvcRequestBuilders.delete(URL + "/{repairId}/pieces/{pieceId}", repairId, pieceId))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isNoContent());
    }

    @Test
    @DisplayName("DELETE /v1/repairs/25256595/pieces/1 throws NotFoundException when repair is not found")
    @Order(35)
    void removesRepairPieceById_ThrowsNotFoundException_WhenRepairIsNotFound() throws Exception {
        Long randomRepairId = 25256595L;
        Piece piece = PieceUtils.newPieceList().getFirst();
        Long pieceId = piece.getId();

        BDDMockito.doThrow(new NotFoundException("Repair not Found")).when(service).removesRepairPieceById(randomRepairId, pieceId);

        String expectedResponse = fileUtils.readResourceFile("repair/delete-repairPiece-invalid-repair-404.json");

        mockMvc.perform(MockMvcRequestBuilders.delete(URL + "/{repairId}/pieces/{pieceId}", randomRepairId, pieceId))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect(MockMvcResultMatchers.content().json(expectedResponse));
    }

    @Test
    @DisplayName("DELETE /v1/repairs/1/pieces/25256595 throws NotFoundException when piece is not found")
    @Order(36)
    void removesRepairPieceById_ThrowsNotFoundException_WhenPieceIsNotFound() throws Exception {
        Repair repair = RepairUtils.newRepairList().getFirst();
        Long repairId = repair.getId();
        Long randomPieceId = 25256595L;

        BDDMockito.doThrow(new NotFoundException("Piece not Found")).when(service).removesRepairPieceById(repairId, randomPieceId);

        String expectedResponse = fileUtils.readResourceFile("repair/delete-repairPiece-invalid-piece-404.json");

        mockMvc.perform(MockMvcRequestBuilders.delete(URL + "/{repairId}/pieces/{pieceId}", repairId, randomPieceId))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect(MockMvcResultMatchers.content().json(expectedResponse));
    }

    @Test
    @DisplayName("DELETE /v1/repairs/1/pieces/3 throws NotFoundException when piece is not found in the repair")
    @Order(37)
    void removesRepairPieceById_ThrowsNotFoundException_WhenPieceIsNotFoundInTheRepair() throws Exception {
        Repair repair = RepairUtils.newRepairList().getFirst();
        Long repairId = repair.getId();
        Piece piece = PieceUtils.newPieceList().getLast();
        Long pieceId = piece.getId();

        BDDMockito.doThrow(new NotFoundException("The piece was not found in the repair")).when(service).removesRepairPieceById(repairId, pieceId);

        String expectedResponse = fileUtils.readResourceFile("repair/delete-repairPiece-invalid-repairPiece-404.json");

        mockMvc.perform(MockMvcRequestBuilders.delete(URL + "/{repairId}/pieces/{pieceId}", repairId, pieceId))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect(MockMvcResultMatchers.content().json(expectedResponse));
    }
}