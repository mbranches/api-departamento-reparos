package com.branches.service;

import com.branches.exception.BadRequestException;
import com.branches.exception.NotFoundException;
import com.branches.mapper.RepairEmployeeMapper;
import com.branches.mapper.RepairMapper;
import com.branches.mapper.RepairPieceMapper;
import com.branches.model.*;
import com.branches.repository.RepairRepository;
import com.branches.request.RepairPostRequest;
import com.branches.response.RepairEmployeeByRepairResponse;
import com.branches.response.RepairGetResponse;
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
    @DisplayName("findAllByRepairId returns all repair employees from given repair id when successful")
    @Order(6)
    void findEmployeesByRepairId_ReturnsAllRepairEmployeesFromGivenRepairId_WhenSuccessful() {
        Repair repair = repairList.getFirst();
        Long idToSearch = repair.getId();

        List<RepairEmployee> foundRepairEmployees = List.of(RepairEmployeeUtils.newRepairEmployeeSaved());
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
    @DisplayName("findAllByRepairId returns an empty list when when repair contain no employees")
    @Order(7)
    void findAllByRepairId_ReturnsEmptyList_WhenRepairContainNoEmployees() {
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
    @DisplayName("findAllByRepairId throws NotFoundException when given id is not found")
    @Order(8)
    void findAllByRepairId_ThrowsNotFoundException_WhenGivenIdIsNotFound() {
        Long randomId = 121123L;

        BDDMockito.when(repository.findById(randomId)).thenReturn(Optional.empty());

        Assertions.assertThatThrownBy(() -> service.findEmployeesByRepairId(randomId))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("Repair not Found");
    }

    @Test
    @DisplayName("findAllByClientId returns all client vehicles when successful")
    @Order(9)
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
    @Order(10)
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
    @Order(11)
    void findAllByClientId_ThrowsNotFoundException_WhenClientIsNotFound() {
        Long randomId = 1515151L;

        BDDMockito.when(clientService.findByIdOrThrowsNotFoundException(randomId)).thenThrow(new NotFoundException("Client not Found"));

        Assertions.assertThatThrownBy(() -> service.findAllByClientId(randomId))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("Client not Found");
    }

    @Test
    @DisplayName("save returns saved repair when successful")
    @Order(12)
    void save_ReturnsSavedRepair_WhenSuccessful() {
        RepairPostRequest postRequest = RepairUtils.newRepairPostRequest();

        RepairPiece newRepairPiece = RepairPieceUtils.newRepairPiece();
        RepairPiece savedRepairPiece = RepairPieceUtils.newRepairPiece();
        Piece piece = savedRepairPiece.getPiece();
        piece.setStock(piece.getStock() - savedRepairPiece.getQuantity());

        RepairEmployee newRepairEmployee = RepairEmployeeUtils.newRepairEmployee();
        Repair newRepair = RepairUtils.newRepair();

        BDDMockito.when(clientService.findByIdOrThrowsNotFoundException(postRequest.getClientId())).thenReturn(ClientUtils.newClientToSave());
        BDDMockito.when(vehicleService.findByIdOrThrowsNotFoundException(postRequest.getVehicleId())).thenReturn(VehicleUtils.newVehicleToSave());
        BDDMockito.when(repairPieceMapper.toRepairPieceList(postRequest.getPieces())).thenReturn(List.of(newRepairPiece));
        BDDMockito.when(repairEmployeeMapper.toRepairEmployeeList(postRequest.getEmployees())).thenReturn(List.of(newRepairEmployee));
        BDDMockito.when(repository.save(ArgumentMatchers.any(Repair.class))).thenReturn(newRepair);
        BDDMockito.when(repairPieceService.saveAll(ArgumentMatchers.anyList())).thenReturn(List.of(savedRepairPiece));
        BDDMockito.when(repairEmployeeService.saveAll(ArgumentMatchers.anyList())).thenReturn(List.of(newRepairEmployee));
        BDDMockito.when(mapper.toRepairPostResponse(newRepair, List.of(savedRepairPiece), List.of(newRepairEmployee))).thenReturn(RepairUtils.newRepairPostResponse());

        RepairPostResponse expectedResponse = RepairUtils.newRepairPostResponse();

        RepairPostResponse response = service.save(postRequest);

        Assertions.assertThat(response)
                .isNotNull()
                .isEqualTo(expectedResponse);
    }

    @Test
    @DisplayName("save throws NotFoundException when client is not found")
    @Order(13)
    void save_ThrowsNotFoundException_WhenClientIsNotFound() {
        RepairPostRequest postRequest = RepairUtils.newRepairPostRequest();

        BDDMockito.when(clientService.findByIdOrThrowsNotFoundException(postRequest.getClientId())).thenThrow(new NotFoundException("Client not Found"));

        Assertions.assertThatThrownBy(() -> service.save(postRequest))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("Client not Found");
    }

    @Test
    @DisplayName("save throws NotFoundException when vehicle is not found")
    @Order(14)
    void save_ThrowsNotFoundException_WhenVehicleIsNotFound() {
        RepairPostRequest postRequest = RepairUtils.newRepairPostRequest();

        BDDMockito.when(clientService.findByIdOrThrowsNotFoundException(postRequest.getClientId())).thenReturn(ClientUtils.newClientToSave());
        BDDMockito.when(vehicleService.findByIdOrThrowsNotFoundException(postRequest.getVehicleId())).thenThrow(new NotFoundException("Vehicle not Found"));

        Assertions.assertThatThrownBy(() -> service.save(postRequest))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("Vehicle not Found");
    }

    @Test
    @DisplayName("save throws BadRequestException when some piece is not found")
    @Order(15)
    void save_ThrowsBadRequestException_WhenSomePieceIsNotFound() {
        RepairPostRequest postRequest = RepairUtils.newRepairPostRequest();

        BDDMockito.when(clientService.findByIdOrThrowsNotFoundException(postRequest.getClientId())).thenReturn(ClientUtils.newClientToSave());
        BDDMockito.when(vehicleService.findByIdOrThrowsNotFoundException(postRequest.getVehicleId())).thenReturn(VehicleUtils.newVehicleToSave());
        BDDMockito.when(repairPieceMapper.toRepairPieceList(postRequest.getPieces())).thenThrow(new BadRequestException("Error saving pieces"));


        Assertions.assertThatThrownBy(() -> service.save(postRequest))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("Error saving pieces");
    }

    @Test
    @DisplayName("save throws BadRequestException when some employee is not found")
    @Order(16)
    void save_ThrowsBadRequestException_WhenSomeEmployeeIsNotFound() {
        RepairPostRequest postRequest = RepairUtils.newRepairPostRequest();

        BDDMockito.when(clientService.findByIdOrThrowsNotFoundException(postRequest.getClientId())).thenReturn(ClientUtils.newClientToSave());
        BDDMockito.when(repairPieceMapper.toRepairPieceList(postRequest.getPieces())).thenReturn(List.of(RepairPieceUtils.newRepairPiece()));
        BDDMockito.when(repairEmployeeMapper.toRepairEmployeeList(postRequest.getEmployees())).thenThrow(new BadRequestException("Error saving employees"));


        Assertions.assertThatThrownBy(() -> service.save(postRequest))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("Error saving employees");
    }

    @Test
    @DisplayName("save throws BadRequestException when quantity is greater than piece stock")
    @Order(17)
    void save_ThrowsBadRequestException_WhenQuantityIsGreaterThanPieceStock() {
        RepairPostRequest postRequest = RepairUtils.newRepairPostRequest();

        RepairPiece newRepairPiece = RepairPieceUtils.newRepairPiece();

        RepairEmployee newRepairEmployee = RepairEmployeeUtils.newRepairEmployee();
        Repair newRepair = RepairUtils.newRepair();

        BDDMockito.when(clientService.findByIdOrThrowsNotFoundException(postRequest.getClientId())).thenReturn(ClientUtils.newClientToSave());
        BDDMockito.when(vehicleService.findByIdOrThrowsNotFoundException(postRequest.getVehicleId())).thenReturn(VehicleUtils.newVehicleToSave());
        BDDMockito.when(repairPieceMapper.toRepairPieceList(postRequest.getPieces())).thenReturn(List.of(newRepairPiece));
        BDDMockito.when(repairEmployeeMapper.toRepairEmployeeList(postRequest.getEmployees())).thenReturn(List.of(newRepairEmployee));
        BDDMockito.when(repository.save(ArgumentMatchers.any(Repair.class))).thenReturn(newRepair);
        BDDMockito.when(repairPieceService.saveAll(ArgumentMatchers.anyList())).thenThrow(BadRequestException.class);


        Assertions.assertThatThrownBy(() -> service.save(postRequest))
                .isInstanceOf(BadRequestException.class);
    }

    @Test
    @DisplayName("deleteById removes repair when successful")
    @Order(18)
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
    @Order(19)
    void deleteById_ThrowsNotFoundException_WhenGivenIdIsNotFound() {
        Long randomId = 15512366L;

        BDDMockito.when(repository.findById(randomId)).thenReturn(Optional.empty());

        Assertions.assertThatThrownBy(() -> service.deleteById(randomId))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("Repair not Found");
    }

    @Test
    @DisplayName("removesRepairEmployeeById removes employee from repair when successful")
    @Order(20)
    void removesRepairEmployeeById_RemovesEmployeeFromRepair_WhenSuccessful() {
        Repair repair = RepairUtils.newRepairList().getFirst();
        Long repairId = repair.getId();

        Employee employee = EmployeeUtils.newEmployeeList().getFirst();
        Long employeeId = employee.getId();

        BDDMockito.when(repository.findById(repairId)).thenReturn(Optional.of(repair));
        BDDMockito.when(employeeService.findByIdOrNotFoundException(employeeId)).thenReturn(employee);
        BDDMockito.doNothing().when(repairEmployeeService).deleteByRepairAndEmployee(repair, employee);

        Assertions.assertThatCode(() -> service.removesRepairEmployeeById(repairId, employeeId))
                .doesNotThrowAnyException();
    }

    @Test
    @DisplayName("removesRepairEmployeeById throws NotFoundException when repair is not found")
    @Order(21)
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
    @Order(22)
    void removesRepairEmployeeById_ThrowsNotFoundException_WhenEmployeeIsNotFound() {
        Repair repair = RepairUtils.newRepairList().getFirst();
        Long repairId = repair.getId();

        Long randomEmployeeId = 5514121L;

        BDDMockito.when(repository.findById(repairId)).thenReturn(Optional.of(repair));
        BDDMockito.when(employeeService.findByIdOrNotFoundException(randomEmployeeId)).thenThrow(new NotFoundException("Employee not Found"));

        Assertions.assertThatThrownBy(() -> service.removesRepairEmployeeById(repairId, randomEmployeeId))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("Employee not Found");
    }

    @Test
    @DisplayName("removesRepairEmployeeById throws NotFoundException when employee is not found in the repair")
    @Order(23)
    void removesRepairEmployeeById_ThrowsNotFoundException_WhenEmployeeIsNotFoundInTheRepair() {
        Repair repair = RepairUtils.newRepairList().getFirst();
        Long repairId = repair.getId();

        Employee employee = EmployeeUtils.newEmployeeList().getLast();
        Long employeeId = employee.getId();

        BDDMockito.when(repository.findById(repairId)).thenReturn(Optional.of(repair));
        BDDMockito.when(employeeService.findByIdOrNotFoundException(employeeId)).thenReturn(employee);
        BDDMockito.doThrow(new NotFoundException("The employee was not found in the repair")).when(repairEmployeeService).deleteByRepairAndEmployee(repair, employee);

        Assertions.assertThatCode(() -> service.removesRepairEmployeeById(repairId, employeeId))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("The employee was not found in the repair");
    }
}