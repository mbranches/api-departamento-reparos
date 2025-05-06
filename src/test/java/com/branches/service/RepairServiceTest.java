package com.branches.service;

import com.branches.exception.BadRequestException;
import com.branches.exception.NotFoundException;
import com.branches.mapper.RepairEmployeeMapper;
import com.branches.mapper.RepairMapper;
import com.branches.mapper.RepairPieceMapper;
import com.branches.model.*;
import com.branches.repository.RepairRepository;
import com.branches.request.RepairEmployeeByRepairPostRequest;
import com.branches.request.RepairPieceByRepairPostRequest;
import com.branches.request.RepairPostRequest;
import com.branches.response.RepairEmployeeByRepairResponse;
import com.branches.response.RepairGetResponse;
import com.branches.response.RepairPieceByRepairResponse;
import com.branches.response.RepairPostResponse;
import com.branches.utils.*;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.BDDMockito;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class RepairServiceTest {
    @InjectMocks
    private RepairService service;
    @Mock
    private RepairRepository repository;
    @Mock
    private RepairMapper mapper;
    @Mock
    private RepairPieceMapper repairPieceMapper;
    @Mock
    private RepairEmployeeMapper repairEmployeeMapper;
    @Mock
    private ClientService clientService;
    @Mock
    private VehicleService vehicleService;
    @Mock
    private RepairPieceService repairPieceService;
    @Mock
    private RepairEmployeeService repairEmployeeService;
    @Mock
    private EmployeeService employeeService;
    @Mock
    private PieceService pieceService;
    private List<Repair> repairList;
    private List<RepairGetResponse> repairGetResponseList;

    @BeforeEach
    void init() {
        repairList = RepairUtils.newRepairList();
        repairGetResponseList = RepairUtils.newRepairGetResponseList();
    }

    @Test
    @DisplayName("findAll returns all repairs when successful")
    @Order(1)
    void findAll_ReturnsAllRepairs_WhenArgumentIsNull() {
        BDDMockito.when(repository.findAll()).thenReturn(repairList);
        BDDMockito.when(mapper.toRepairGetResponseList(repairList)).thenReturn(repairGetResponseList);

        List<RepairGetResponse> response = service.findAll(null);

        Assertions.assertThat(response)
                .isNotNull()
                .isNotEmpty()
                .containsExactlyElementsOf(repairGetResponseList);
    }

    @Test
    @DisplayName("findAll returns all repairs in date range when argument is given")
    @Order(2)
    void findAll_ReturnsAllRepairsInDateRange_WhenArgumentIsGiven() {
        LocalDate dateToSearch = LocalDate.of(2025, 2, 12);

        BDDMockito.when(repository.findByEndDateGreaterThanEqual(dateToSearch)).thenReturn(repairList);
        BDDMockito.when(mapper.toRepairGetResponseList(repairList)).thenReturn(repairGetResponseList);

        List<RepairGetResponse> response = service.findAll(dateToSearch);

        Assertions.assertThat(response)
                .isNotNull()
                .isNotEmpty()
                .containsExactlyElementsOf(repairGetResponseList);
    }

    @Test
    @DisplayName("findAll returns an empty list when there are no repairs in date range")
    @Order(3)
    void findAll_ReturnsEmptyList_WhenThereAreNoRepairsInDateRange() {
        LocalDate dateToSearch = LocalDate.of(2026, 12, 15);

        BDDMockito.when(repository.findByEndDateGreaterThanEqual(dateToSearch)).thenReturn(Collections.emptyList());
        BDDMockito.when(mapper.toRepairGetResponseList(Collections.emptyList())).thenReturn(Collections.emptyList());

        List<RepairGetResponse> response = service.findAll(dateToSearch);

        Assertions.assertThat(response)
                .isNotNull()
                .isEmpty();
    }

    @Test
    @DisplayName("findById returns found repair when successful")
    @Order(4)
    void findById_ReturnsFoundRepair_WhenSuccessful() {
        Repair expectedResponseRepository = repairList.getFirst();
        Long idToSearch = expectedResponseRepository.getId();

        RepairGetResponse expectedResponse = repairGetResponseList.getFirst();

        BDDMockito.when(repository.findById(idToSearch)).thenReturn(Optional.of(expectedResponseRepository));
        BDDMockito.when(mapper.toRepairGetResponse(expectedResponseRepository)).thenReturn(expectedResponse);

        RepairGetResponse response = service.findById(idToSearch);

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
                .hasMessageContaining("Repair not Found");
    }

    @Test
    @DisplayName("findEmployeesByRepairId returns all repair employees from given repair id when successful")
    @Order(6)
    void findEmployeesByRepairId_ReturnsAllRepairEmployeesFromGivenRepairId_WhenSuccessful() {
        Repair repair = repairList.getFirst();
        Long idToSearch = repair.getId();

        RepairEmployee repairEmployee = RepairEmployeeUtils.newRepairEmployeeList().getFirst();
        List<RepairEmployee> foundRepairEmployees = List.of(repairEmployee);
        List<RepairEmployeeByRepairResponse> expectedResponse = List.of(RepairEmployeeUtils.newRepairEmployeeByRepairGetEmployees());

        BDDMockito.when(repository.findById(idToSearch)).thenReturn(Optional.of(repair));
        BDDMockito.when(repairEmployeeService.findAllByRepair(repair)).thenReturn(foundRepairEmployees);
        BDDMockito.when(repairEmployeeMapper.toRepairEmployeeByRepairResponseList(foundRepairEmployees)).thenReturn(expectedResponse);

        List<RepairEmployeeByRepairResponse> response = service.findEmployeesByRepairId(idToSearch);

        Assertions.assertThat(response)
                .isNotNull()
                .isNotEmpty()
                .containsExactlyElementsOf(expectedResponse);
    }

    @Test
    @DisplayName("findEmployeesByRepairId returns an empty list when when repair contain no employees")
    @Order(7)
    void findEmployeesByRepairId_ReturnsEmptyList_WhenRepairContainNoEmployees() {
        Repair repair = repairList.getLast();
        Long idToSearch = repair.getId();

        BDDMockito.when(repository.findById(idToSearch)).thenReturn(Optional.of(repair));
        BDDMockito.when(repairEmployeeService.findAllByRepair(repair)).thenReturn(Collections.emptyList());
        BDDMockito.when(repairEmployeeMapper.toRepairEmployeeByRepairResponseList(Collections.emptyList())).thenReturn(Collections.emptyList());

        List<RepairEmployeeByRepairResponse> response = service.findEmployeesByRepairId(idToSearch);

        Assertions.assertThat(response)
                .isNotNull()
                .isEmpty();
    }

    @Test
    @DisplayName("findEmployeesByRepairId throws NotFoundException when given id is not found")
    @Order(8)
    void findEmployeesByRepairId_ThrowsNotFoundException_WhenGivenIdIsNotFound() {
        Long randomId = 121123L;

        BDDMockito.when(repository.findById(randomId)).thenReturn(Optional.empty());

        Assertions.assertThatThrownBy(() -> service.findEmployeesByRepairId(randomId))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("Repair not Found");
    }

    @Test
    @DisplayName("findPiecesByRepairId returns all repair pieces from given repair id when successful")
    @Order(9)
    void findPiecesByRepairId_ReturnsAllRepairPiecesFromGivenRepairId_WhenSuccessful() {
        Repair repair = repairList.getFirst();
        Long idToSearch = repair.getId();

        RepairPiece repairPiece = RepairPieceUtils.newRepairPieceList().getFirst();
        List<RepairPiece> foundRepairPieces = List.of(repairPiece);
        List<RepairPieceByRepairResponse> expectedResponse = List.of(RepairPieceUtils.newRepairPieceByRepairGetPieces());

        BDDMockito.when(repository.findById(idToSearch)).thenReturn(Optional.of(repair));
        BDDMockito.when(repairPieceService.findAllByRepair(repair)).thenReturn(foundRepairPieces);
        BDDMockito.when(repairPieceMapper.toRepairPieceByRepairResponseList(foundRepairPieces)).thenReturn(expectedResponse);

        List<RepairPieceByRepairResponse> response = service.findPiecesByRepairId(idToSearch);

        Assertions.assertThat(response)
                .isNotNull()
                .isNotEmpty()
                .containsExactlyElementsOf(expectedResponse);
    }

    @Test
    @DisplayName("findPiecesByRepairId returns an empty list when when repair contain no pieces")
    @Order(10)
    void findPiecesByRepairId_ReturnsEmptyList_WhenRepairContainNoPieces() {
        Repair repair = repairList.getLast();
        Long idToSearch = repair.getId();

        BDDMockito.when(repository.findById(idToSearch)).thenReturn(Optional.of(repair));
        BDDMockito.when(repairPieceService.findAllByRepair(repair)).thenReturn(Collections.emptyList());
        BDDMockito.when(repairPieceMapper.toRepairPieceByRepairResponseList(Collections.emptyList())).thenReturn(Collections.emptyList());

        List<RepairPieceByRepairResponse> response = service.findPiecesByRepairId(idToSearch);

        Assertions.assertThat(response)
                .isNotNull()
                .isEmpty();
    }

    @Test
    @DisplayName("findPiecesByRepairId throws NotFoundException when given id is not found")
    @Order(11)
    void findPiecesByRepairId_ThrowsNotFoundException_WhenGivenIdIsNotFound() {
        Long randomId = 121123L;

        BDDMockito.when(repository.findById(randomId)).thenReturn(Optional.empty());

        Assertions.assertThatThrownBy(() -> service.findPiecesByRepairId(randomId))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("Repair not Found");
    }

    @Test
    @DisplayName("findAllByClientId returns all client vehicles when successful")
    @Order(12)
    void findAllByClientId_ReturnsAllClientRepairs_WhenSuccessful() {
        Client client = repairList.getFirst().getClient();
        Long clientId = client.getId();

        BDDMockito.when(clientService.findByIdOrThrowsNotFoundException(clientId)).thenReturn(client);
        BDDMockito.when(repository.findAllByClient(client)).thenReturn(repairList);
        BDDMockito.when(mapper.toRepairGetResponseList(repairList)).thenReturn(repairGetResponseList);

        List<RepairGetResponse> response = service.findAllByClientId(clientId);

        Assertions.assertThat(response)
                .isNotNull()
                .isNotEmpty()
                .containsExactlyElementsOf(repairGetResponseList);
    }

    @Test
    @DisplayName("findAllByClientId returns an empty list when client doesn't have repairs")
    @Order(13)
    void findAllByClientId_ReturnsEmptyList_WhenClientDoesNotHaveRepair() {
        Client client = ClientUtils.newClientList().getLast();
        Long clientId = client.getId();

        BDDMockito.when(clientService.findByIdOrThrowsNotFoundException(clientId)).thenReturn(client);
        BDDMockito.when(repository.findAllByClient(client)).thenReturn(Collections.emptyList());
        BDDMockito.when(mapper.toRepairGetResponseList(Collections.emptyList())).thenReturn(Collections.emptyList());

        List<RepairGetResponse> response = service.findAllByClientId(clientId);

        Assertions.assertThat(response)
                .isNotNull()
                .isEmpty();
    }

    @Test
    @DisplayName("findAllByClientId throws NotFoundException when client is not found")
    @Order(14)
    void findAllByClientId_ThrowsNotFoundException_WhenClientIsNotFound() {
        Long randomId = 1515151L;

        BDDMockito.when(clientService.findByIdOrThrowsNotFoundException(randomId)).thenThrow(new NotFoundException("Client not Found"));

        Assertions.assertThatThrownBy(() -> service.findAllByClientId(randomId))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("Client not Found");
    }

    @Test
    @DisplayName("save returns saved repair when successful")
    @Order(15)
    void save_ReturnsSavedRepair_WhenSuccessful() {
        RepairPostRequest postRequest = RepairUtils.newRepairPostRequest();

        RepairPiece repairPieceToSave = RepairPieceUtils.newRepairPieceToSave();

        RepairPiece savedRepairPiece = RepairPieceUtils.newRepairPieceSaved();
        Piece piece = savedRepairPiece.getPiece();
        piece.setStock(piece.getStock() - savedRepairPiece.getQuantity());

        RepairEmployee repairEmployeeToSave = RepairEmployeeUtils.newRepairEmployeeToSave();
        RepairEmployee savedRepairEmployee = RepairEmployeeUtils.newRepairEmployeeSaved();

        Repair repairToSave = RepairUtils.newRepairToSave();
        Repair savedRepair = RepairUtils.newRepairSaved();

        BDDMockito.when(clientService.findByIdOrThrowsNotFoundException(postRequest.getClientId())).thenReturn(ClientUtils.newClientSaved());
        BDDMockito.when(vehicleService.findByIdOrThrowsNotFoundException(postRequest.getVehicleId())).thenReturn(VehicleUtils.newVehicleToSave());
        BDDMockito.when(repairPieceMapper.toRepairPieceList(postRequest.getPieces())).thenReturn(List.of(repairPieceToSave));
        BDDMockito.when(repairEmployeeMapper.toRepairEmployeeList(postRequest.getEmployees())).thenReturn(List.of(repairEmployeeToSave));
        BDDMockito.when(repository.save(repairToSave.withTotalValue(250))).thenReturn(savedRepair);
        BDDMockito.when(repairPieceService.saveAll(List.of(repairPieceToSave))).thenReturn(List.of(savedRepairPiece));
        BDDMockito.when(repairEmployeeService.saveAll(List.of(repairEmployeeToSave))).thenReturn(List.of(savedRepairEmployee));
        BDDMockito.when(mapper.toRepairPostResponse(savedRepair, List.of(savedRepairPiece), List.of(savedRepairEmployee))).thenReturn(RepairUtils.newRepairPostResponse());

        RepairPostResponse expectedResponse = RepairUtils.newRepairPostResponse();

        RepairPostResponse response = service.save(postRequest);

        Assertions.assertThat(response)
                .isNotNull()
                .isEqualTo(expectedResponse);
    }

    @Test
    @DisplayName("save throws NotFoundException when client is not found")
    @Order(16)
    void save_ThrowsNotFoundException_WhenClientIsNotFound() {
        RepairPostRequest postRequest = RepairUtils.newRepairPostRequest();

        BDDMockito.when(clientService.findByIdOrThrowsNotFoundException(postRequest.getClientId())).thenThrow(new NotFoundException("Client not Found"));

        Assertions.assertThatThrownBy(() -> service.save(postRequest))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("Client not Found");
    }

    @Test
    @DisplayName("save throws NotFoundException when vehicle is not found")
    @Order(17)
    void save_ThrowsNotFoundException_WhenVehicleIsNotFound() {
        RepairPostRequest postRequest = RepairUtils.newRepairPostRequest().withClientId(999L);

        BDDMockito.when(clientService.findByIdOrThrowsNotFoundException(postRequest.getClientId())).thenReturn(ClientUtils.newClientSaved());
        BDDMockito.when(vehicleService.findByIdOrThrowsNotFoundException(postRequest.getVehicleId())).thenThrow(new NotFoundException("Vehicle not Found"));

        Assertions.assertThatThrownBy(() -> service.save(postRequest))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("Vehicle not Found");
    }

    @Test
    @DisplayName("save throws BadRequestException when some piece is not found")
    @Order(18)
    void save_ThrowsBadRequestException_WhenSomePieceIsNotFound() {
        RepairPostRequest postRequest = RepairUtils.newRepairPostRequest();
        postRequest.getPieces().forEach(piece -> piece.setPieceId(999L));

        BDDMockito.when(clientService.findByIdOrThrowsNotFoundException(postRequest.getClientId())).thenReturn(ClientUtils.newClientSaved());
        BDDMockito.when(vehicleService.findByIdOrThrowsNotFoundException(postRequest.getVehicleId())).thenReturn(VehicleUtils.newVehicleToSave());
        BDDMockito.when(repairPieceMapper.toRepairPieceList(postRequest.getPieces())).thenThrow(new BadRequestException("Error saving pieces"));


        Assertions.assertThatThrownBy(() -> service.save(postRequest))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("Error saving pieces");
    }

    @Test
    @DisplayName("save throws BadRequestException when some employee is not found")
    @Order(19)
    void save_ThrowsBadRequestException_WhenSomeEmployeeIsNotFound() {
        RepairPostRequest postRequest = RepairUtils.newRepairPostRequest();
        postRequest.getEmployees().forEach(employee -> employee.setEmployeeId(999L));

        BDDMockito.when(clientService.findByIdOrThrowsNotFoundException(postRequest.getClientId())).thenReturn(ClientUtils.newClientSaved());
        BDDMockito.when(repairPieceMapper.toRepairPieceList(postRequest.getPieces())).thenReturn(List.of(RepairPieceUtils.newRepairPieceToSave()));
        BDDMockito.when(repairEmployeeMapper.toRepairEmployeeList(postRequest.getEmployees())).thenThrow(new BadRequestException("Error saving employees"));


        Assertions.assertThatThrownBy(() -> service.save(postRequest))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("Error saving employees");
    }

    @Test
    @DisplayName("save throws BadRequestException when quantity is greater than piece stock")
    @Order(20)
    void save_ThrowsBadRequestException_WhenQuantityIsGreaterThanPieceStock() {
        RepairPostRequest postRequest = RepairUtils.newRepairPostRequest();

        RepairPiece repairPieceToSave = RepairPieceUtils.newRepairPieceToSave();

        RepairEmployee repairEmployeeToSave = RepairEmployeeUtils.newRepairEmployeeToSave();
        Repair repairToSave = RepairUtils.newRepairToSave();
        Repair savedRepair = RepairUtils.newRepairSaved();

        BDDMockito.when(clientService.findByIdOrThrowsNotFoundException(postRequest.getClientId())).thenReturn(ClientUtils.newClientSaved());
        BDDMockito.when(vehicleService.findByIdOrThrowsNotFoundException(postRequest.getVehicleId())).thenReturn(VehicleUtils.newVehicleToSave());
        BDDMockito.when(repairPieceMapper.toRepairPieceList(postRequest.getPieces())).thenReturn(List.of(repairPieceToSave));
        BDDMockito.when(repairEmployeeMapper.toRepairEmployeeList(postRequest.getEmployees())).thenReturn(List.of(repairEmployeeToSave));
        BDDMockito.when(repository.save(repairToSave.withTotalValue(250))).thenReturn(savedRepair);
        BDDMockito.when(repairPieceService.saveAll(List.of(repairPieceToSave))).thenThrow(BadRequestException.class);


        Assertions.assertThatThrownBy(() -> service.save(postRequest))
                .isInstanceOf(BadRequestException.class);
    }

    @Test
    @DisplayName("addEmployee returns all saved RepairEmployees when the given employee is not registered")
    @Order(21)
    void addEmployee_ReturnsAllSavedRepairEmployees_WhenTheGivenEmployeeIsNotRegistered() {
        Repair repair = RepairUtils.newRepairList().getFirst();
        Long repairId = repair.getId();

        RepairEmployeeByRepairPostRequest repairEmployeeToSaved = RepairEmployeeUtils.newRepairEmployeePostRequest();
        List<RepairEmployeeByRepairPostRequest> repairEmployeePostRequestList = List.of(repairEmployeeToSaved);
        List<RepairEmployee> repairEmployeeToSaveList = List.of(RepairEmployeeUtils.newRepairEmployeeToSave());

        List<RepairEmployeeByRepairResponse> expectedResponse = List.of(RepairEmployeeUtils.newRepairEmployeeByRepairPostResponse());

        BDDMockito.when(repository.findById(repairId)).thenReturn(Optional.of(repair));
        BDDMockito.when(repairEmployeeMapper.toRepairEmployeeList(repairEmployeePostRequestList)).thenReturn(repairEmployeeToSaveList);
        BDDMockito.when(repairEmployeeService.findAllByRepair(repair)).thenReturn(new ArrayList<>());
        BDDMockito.when(repairEmployeeService.saveAll(repairEmployeeToSaveList)).thenReturn(repairEmployeeToSaveList);
        BDDMockito.when(repairEmployeeMapper.toRepairEmployeeByRepairResponseList(repairEmployeeToSaveList)).thenReturn(expectedResponse);

        List<RepairEmployeeByRepairResponse> response = service.addEmployee(repairId, repairEmployeePostRequestList);

        Assertions.assertThat(response)
                .isNotNull()
                .isNotEmpty()
                .containsExactlyElementsOf(expectedResponse);
    }

    @Test
    @DisplayName("addEmployee returns all saved RepairEmployees when the given employee is registered")
    @Order(22)
    void addEmployee_ReturnsAllSavedRepairEmployees_WhenGivenEmployeeIsRegistered() {
        Repair repair = RepairUtils.newRepairList().getFirst();
        Long repairId = repair.getId();

        RepairEmployeeByRepairPostRequest repairEmployeeToSaved = RepairEmployeeUtils.newRepairEmployeePostRequestWithRegisteredEmployee();
        repairEmployeeToSaved.setHoursWorked(4);
        List<RepairEmployeeByRepairPostRequest> repairEmployeePostRequestList = List.of(repairEmployeeToSaved);
        List<RepairEmployee> repairEmployeeToSaveList = List.of(RepairEmployeeUtils.newRepairEmployeeList().getFirst());

        List<RepairEmployeeByRepairResponse> expectedResponse = List.of(RepairEmployeeUtils.newRepairEmployeeByRepairByAddEmployee());

        BDDMockito.when(repository.findById(repairId)).thenReturn(Optional.of(repair));
        BDDMockito.when(repairEmployeeMapper.toRepairEmployeeList(repairEmployeePostRequestList)).thenReturn(repairEmployeeToSaveList);
        BDDMockito.when(repairEmployeeService.findAllByRepair(repair)).thenReturn(repairEmployeeToSaveList);
        BDDMockito.when(repairEmployeeService.saveAll(repairEmployeeToSaveList)).thenReturn(repairEmployeeToSaveList);
        BDDMockito.when(repairEmployeeMapper.toRepairEmployeeByRepairResponseList(repairEmployeeToSaveList)).thenReturn(expectedResponse);

        List<RepairEmployeeByRepairResponse> response = service.addEmployee(repairId, repairEmployeePostRequestList);

        Assertions.assertThat(response)
                .isNotNull()
                .isNotEmpty()
                .containsExactlyElementsOf(expectedResponse);
    }

    @Test
    @DisplayName("addEmployee throws NotFoundException when repairId is not found")
    @Order(23)
    void addEmployee_ThrowsNotFoundException_WhenRepairIdIsNotFound() {
        Long randomRepairId = 14281267L;

        RepairEmployeeByRepairPostRequest repairEmployeeToSaved = RepairEmployeeUtils.newRepairEmployeePostRequest();
        List<RepairEmployeeByRepairPostRequest> repairEmployeePostRequestList = List.of(repairEmployeeToSaved);

        BDDMockito.when(repository.findById(randomRepairId)).thenReturn(Optional.empty());

        Assertions.assertThatThrownBy(() -> service.addEmployee(randomRepairId, repairEmployeePostRequestList))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("Repair not Found");
    }

    @Test
    @DisplayName("addEmployee throws BadRequestException when some given employee is not found")
    @Order(24)
    void addEmployee_ThrowsBadRequestException_WhenSomeGivenEmployeeIsNotFound() {
        Repair repair = RepairUtils.newRepairList().getFirst();
        Long repairId = repair.getId();

        RepairEmployeeByRepairPostRequest repairEmployeeToSaved = RepairEmployeeUtils.newRepairEmployeePostRequest();
        List<RepairEmployeeByRepairPostRequest> repairEmployeePostRequestList = List.of(repairEmployeeToSaved);

        BDDMockito.when(repository.findById(repairId)).thenReturn(Optional.of(repair));
        BDDMockito.when(repairEmployeeMapper.toRepairEmployeeList(repairEmployeePostRequestList)).thenThrow(new BadRequestException("Error saving employees"));

        Assertions.assertThatThrownBy(() -> service.addEmployee(repairId, repairEmployeePostRequestList))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("Error saving employees");
    }

    @Test
    @DisplayName("addPiece returns all saved RepairPieces when successful")
    @Order(25)
    void addPiece_ReturnsAllSavedRepairPieces_WhenTheGivenPieceIsNotRegistered() {
        Repair repair = RepairUtils.newRepairList().getFirst();
        Long repairId = repair.getId();

        RepairPieceByRepairPostRequest repairPieceToSaved = RepairPieceUtils.newRepairPiecePostRequest();

        RepairPiece repairPieceSaved = RepairPieceUtils.newRepairPieceToSave();
        repairPieceSaved.setRepair(repair);


        List<RepairPieceByRepairPostRequest> repairPiecePostRequestList = List.of(repairPieceToSaved);
        List<RepairPiece> repairPieceToSaveList = List.of(RepairPieceUtils.newRepairPieceToSave());

        List<RepairPieceByRepairResponse> expectedResponse = List.of(RepairPieceUtils.newRepairPieceByRepairPostResponse());

        BDDMockito.when(repository.findById(repairId)).thenReturn(Optional.of(repair));
        BDDMockito.when(repairPieceMapper.toRepairPieceList(repairPiecePostRequestList)).thenReturn(repairPieceToSaveList);
        BDDMockito.when(repairPieceService.save(repairPieceSaved)).thenReturn(repairPieceSaved);
        BDDMockito.when(repairPieceMapper.toRepairPieceByRepairResponseList(repairPieceToSaveList)).thenReturn(expectedResponse);

        List<RepairPieceByRepairResponse> response = service.addPiece(repairId, repairPiecePostRequestList);

        Assertions.assertThat(response)
                .isNotNull()
                .isNotEmpty()
                .containsExactlyElementsOf(expectedResponse);
    }

    @Test
    @DisplayName("addPiece throws NotFoundException when repairId is not found")
    @Order(26)
    void addPiece_ThrowsNotFoundException_WhenSomeGivenPieceIsNotFound() {
        Long randomRepairId = 14281267L;

        RepairPieceByRepairPostRequest repairPieceToSaved = RepairPieceUtils.newRepairPiecePostRequest();
        List<RepairPieceByRepairPostRequest> repairPiecePostRequestList = List.of(repairPieceToSaved);

        BDDMockito.when(repository.findById(randomRepairId)).thenReturn(Optional.empty());

        Assertions.assertThatThrownBy(() -> service.addPiece(randomRepairId, repairPiecePostRequestList))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("Repair not Found");
    }

    @Test
    @DisplayName("addPiece throws BadRequestException when some given piece is not found")
    @Order(27)
    void addPiece_ThrowsBadRequestException_WhenSomeGivenPieceIsNotFound() {
        Repair repair = RepairUtils.newRepairList().getFirst();
        Long repairId = repair.getId();

        RepairPieceByRepairPostRequest repairPieceToSaved = RepairPieceUtils.newRepairPiecePostRequest();
        List<RepairPieceByRepairPostRequest> repairPiecePostRequestList = List.of(repairPieceToSaved);

        BDDMockito.when(repository.findById(repairId)).thenReturn(Optional.of(repair));
        BDDMockito.when(repairPieceMapper.toRepairPieceList(repairPiecePostRequestList)).thenThrow(new BadRequestException("Error saving pieces"));

        Assertions.assertThatThrownBy(() -> service.addPiece(repairId, repairPiecePostRequestList))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("Error saving pieces");
    }

    @Test
    @DisplayName("deleteById removes repair when successful")
    @Order(28)
    void deleteById_RemovesRepair_WhenSuccessful() {
        Repair repairToDelete = repairList.getFirst();
        Long idToDelete = repairToDelete.getId();

        BDDMockito.when(repository.findById(idToDelete)).thenReturn(Optional.of(repairToDelete));
        BDDMockito.doNothing().when(repository).delete(ArgumentMatchers.any(Repair.class));

        Assertions.assertThatCode(() -> service.deleteById(idToDelete))
                .doesNotThrowAnyException();
    }

    @Test
    @DisplayName("deleteById throws NotFoundException when given id is not found")
    @Order(29)
    void deleteById_ThrowsNotFoundException_WhenGivenIdIsNotFound() {
        Long randomId = 15512366L;

        BDDMockito.when(repository.findById(randomId)).thenReturn(Optional.empty());

        Assertions.assertThatThrownBy(() -> service.deleteById(randomId))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("Repair not Found");
    }

    @Test
    @DisplayName("removesRepairEmployeeById removes employee from repair when successful")
    @Order(30)
    void removesRepairEmployeeById_RemovesEmployeeFromRepair_WhenSuccessful() {
        Repair repair = RepairUtils.newRepairList().getFirst();
        Long repairId = repair.getId();

        Employee employee = EmployeeUtils.newEmployeeList().getFirst();
        Long employeeId = employee.getId();

        BDDMockito.when(repository.findById(repairId)).thenReturn(Optional.of(repair));
        BDDMockito.when(employeeService.findByIdOrThrowsNotFoundException(employeeId)).thenReturn(employee);
        BDDMockito.doNothing().when(repairEmployeeService).deleteByRepairAndEmployee(repair, employee);

        Assertions.assertThatCode(() -> service.removesRepairEmployeeById(repairId, employeeId))
                .doesNotThrowAnyException();
    }

    @Test
    @DisplayName("removesRepairEmployeeById throws NotFoundException when repair is not found")
    @Order(31)
    void removesRepairEmployeeById_ThrowsNotFoundException_WhenRepairIsNotFound() {
        Long randomRepairId = 5514121L;

        Employee employee = EmployeeUtils.newEmployeeList().getFirst();
        Long employeeId = employee.getId();


        BDDMockito.when(repository.findById(randomRepairId)).thenReturn(Optional.empty());

        Assertions.assertThatThrownBy(() -> service.removesRepairEmployeeById(randomRepairId, employeeId))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("Repair not Found");
    }

    @Test
    @DisplayName("removesRepairEmployeeById throws NotFoundException when employee is not found")
    @Order(32)
    void removesRepairEmployeeById_ThrowsNotFoundException_WhenEmployeeIsNotFound() {
        Repair repair = RepairUtils.newRepairList().getFirst();
        Long repairId = repair.getId();

        Long randomEmployeeId = 5514121L;

        BDDMockito.when(repository.findById(repairId)).thenReturn(Optional.of(repair));
        BDDMockito.when(employeeService.findByIdOrThrowsNotFoundException(randomEmployeeId)).thenThrow(new NotFoundException("Employee not Found"));

        Assertions.assertThatThrownBy(() -> service.removesRepairEmployeeById(repairId, randomEmployeeId))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("Employee not Found");
    }

    @Test
    @DisplayName("removesRepairEmployeeById throws NotFoundException when employee is not found in the repair")
    @Order(33)
    void removesRepairEmployeeById_ThrowsNotFoundException_WhenEmployeeIsNotFoundInTheRepair() {
        Repair repair = RepairUtils.newRepairList().getFirst();
        Long repairId = repair.getId();

        Employee employee = EmployeeUtils.newEmployeeList().getLast();
        Long employeeId = employee.getId();

        BDDMockito.when(repository.findById(repairId)).thenReturn(Optional.of(repair));
        BDDMockito.when(employeeService.findByIdOrThrowsNotFoundException(employeeId)).thenReturn(employee);
        BDDMockito.doThrow(new NotFoundException("The employee was not found in the repair")).when(repairEmployeeService).deleteByRepairAndEmployee(repair, employee);

        Assertions.assertThatCode(() -> service.removesRepairEmployeeById(repairId, employeeId))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("The employee was not found in the repair");
    }

    @Test
    @DisplayName("removesRepairPieceById removes piece from repair when successful")
    @Order(34)
    void removesRepairPieceById_RemovesPieceFromRepair_WhenSuccessful() {
        Repair repair = RepairUtils.newRepairList().getFirst();
        Long repairId = repair.getId();

        Piece piece = PieceUtils.newPieceList().getFirst();
        Long pieceId = piece.getId();

        BDDMockito.when(repository.findById(repairId)).thenReturn(Optional.of(repair));
        BDDMockito.when(pieceService.findByIdOrThrowsNotFoundException(pieceId)).thenReturn(piece);
        BDDMockito.doNothing().when(repairPieceService).deleteByRepairAndPiece(repair, piece);

        Assertions.assertThatCode(() -> service.removesRepairPieceById(repairId, pieceId))
                .doesNotThrowAnyException();
    }

    @Test
    @DisplayName("removesRepairPieceById throws NotFoundException when repair is not found")
    @Order(35)
    void removesRepairPieceById_ThrowsNotFoundException_WhenRepairIsNotFound() {
        Long randomRepairId = 5514121L;

        Piece piece = PieceUtils.newPieceList().getFirst();
        Long pieceId = piece.getId();


        BDDMockito.when(repository.findById(randomRepairId)).thenReturn(Optional.empty());

        Assertions.assertThatThrownBy(() -> service.removesRepairPieceById(randomRepairId, pieceId))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("Repair not Found");
    }

    @Test
    @DisplayName("removesRepairPieceById throws NotFoundException when piece is not found")
    @Order(36)
    void removesRepairPieceById_ThrowsNotFoundException_WhenPieceIsNotFound() {
        Repair repair = RepairUtils.newRepairList().getFirst();
        Long repairId = repair.getId();

        Long randomPieceId = 5514121L;

        BDDMockito.when(repository.findById(repairId)).thenReturn(Optional.of(repair));
        BDDMockito.when(pieceService.findByIdOrThrowsNotFoundException(randomPieceId)).thenThrow(new NotFoundException("Piece not Found"));

        Assertions.assertThatThrownBy(() -> service.removesRepairPieceById(repairId, randomPieceId))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("Piece not Found");
    }

    @Test
    @DisplayName("removesRepairPieceById throws NotFoundException when piece is not found in the repair")
    @Order(37)
    void removesRepairPieceById_ThrowsNotFoundException_WhenPieceIsNotFoundInTheRepair() {
        Repair repair = RepairUtils.newRepairList().getFirst();
        Long repairId = repair.getId();

        Piece piece = PieceUtils.newPieceList().getLast();
        Long pieceId = piece.getId();

        BDDMockito.when(repository.findById(repairId)).thenReturn(Optional.of(repair));
        BDDMockito.when(pieceService.findByIdOrThrowsNotFoundException(pieceId)).thenReturn(piece);
        BDDMockito.doThrow(new NotFoundException("The piece was not found in the repair")).when(repairPieceService).deleteByRepairAndPiece(repair, piece);

        Assertions.assertThatCode(() -> service.removesRepairPieceById(repairId, pieceId))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("The piece was not found in the repair");
    }
}