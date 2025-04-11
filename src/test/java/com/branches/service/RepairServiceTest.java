package com.branches.service;

import com.branches.exception.BadRequestException;
import com.branches.exception.NotFoundException;
import com.branches.mapper.RepairEmployeeMapper;
import com.branches.mapper.RepairMapper;
import com.branches.mapper.RepairPieceMapper;
import com.branches.model.Repair;
import com.branches.model.RepairEmployee;
import com.branches.model.RepairPiece;
import com.branches.repository.RepairRepository;
import com.branches.request.RepairPostRequest;
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

import java.util.List;

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
    void findAll_ReturnsAllRepairs_WhenSuccessful() {
        BDDMockito.when(repository.findAll()).thenReturn(repairList);
        BDDMockito.when(mapper.toRepairGetResponseList(repairList)).thenReturn(repairGetResponseList);

        List<RepairGetResponse> response = service.findAll();

        Assertions.assertThat(response)
                .isNotNull()
                .isNotEmpty()
                .containsExactlyElementsOf(repairGetResponseList);
    }

    @Test
    @DisplayName("save returns saved repair when successful")
    @Order(2)
    void save_ReturnsSavedRepair_WhenSuccessful() {
        RepairPostRequest postRequest = RepairUtils.newRepairPostRequest();

        RepairPiece newRepairPiece = RepairPieceUtils.newRepairPiece();
        RepairEmployee newRepairEmployee = RepairEmployeeUtils.newRepairEmployee();
        Repair newRepair = RepairUtils.newRepair();

        BDDMockito.when(clientService.findByIdOrThrowsNotFoundException(postRequest.getClientId())).thenReturn(ClientUtils.newClientToSave());
        BDDMockito.when(vehicleService.findByIdOrThrowsNotFoundException(postRequest.getVehicleId())).thenReturn(VehicleUtils.newVehicleToSave());
        BDDMockito.when(repairPieceMapper.toRepairPieceList(postRequest.getPieces())).thenReturn(List.of(newRepairPiece));
        BDDMockito.when(repairEmployeeMapper.toRepairEmployeeList(postRequest.getEmployees())).thenReturn(List.of(newRepairEmployee));
        BDDMockito.when(repository.save(ArgumentMatchers.any(Repair.class))).thenReturn(newRepair);
        BDDMockito.when(repairPieceService.saveAll(ArgumentMatchers.anyList())).thenReturn(List.of(newRepairPiece));
        BDDMockito.when(repairEmployeeService.saveAll(ArgumentMatchers.anyList())).thenReturn(List.of(newRepairEmployee));
        BDDMockito.when(mapper.toRepairPostResponse(newRepair, List.of(newRepairPiece), List.of(newRepairEmployee))).thenReturn(RepairUtils.newRepairPostResponse());

        RepairPostResponse expectedResponse = RepairUtils.newRepairPostResponse();

        RepairPostResponse response = service.save(postRequest);

        Assertions.assertThat(response)
                .isNotNull()
                .isEqualTo(expectedResponse);
    }

    @Test
    @DisplayName("save throws NotFoundException when client is not found")
    @Order(3)
    void save_ThrowsNotFoundException_WhenClientIsNotFound() {
        RepairPostRequest postRequest = RepairUtils.newRepairPostRequest();

        BDDMockito.when(clientService.findByIdOrThrowsNotFoundException(postRequest.getClientId())).thenThrow(new NotFoundException("Client not Found"));

        Assertions.assertThatThrownBy(() -> service.save(postRequest))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("Client not Found");
    }

    @Test
    @DisplayName("save throws NotFoundException when vehicle is not found")
    @Order(4)
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
    @Order(5)
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
    @Order(6)
    void save_ThrowsBadRequestException_WhenSomeEmployeeIsNotFound() {
        RepairPostRequest postRequest = RepairUtils.newRepairPostRequest();

        BDDMockito.when(clientService.findByIdOrThrowsNotFoundException(postRequest.getClientId())).thenReturn(ClientUtils.newClientToSave());
        BDDMockito.when(repairPieceMapper.toRepairPieceList(postRequest.getPieces())).thenReturn(List.of(RepairPieceUtils.newRepairPiece()));
        BDDMockito.when(repairEmployeeMapper.toRepairEmployeeList(postRequest.getEmployees())).thenThrow(new BadRequestException("Error saving employees"));


        Assertions.assertThatThrownBy(() -> service.save(postRequest))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("Error saving employees");
    }
}