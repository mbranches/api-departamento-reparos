package com.branches.service;

import com.branches.exception.NotFoundException;
import com.branches.mapper.RepairEmployeeMapper;
import com.branches.model.Employee;
import com.branches.model.Repair;
import com.branches.model.RepairEmployee;
import com.branches.repository.RepairEmployeeRepository;
import com.branches.request.RepairEmployeePostRequest;
import com.branches.response.RepairEmployeePostResponse;
import com.branches.utils.EmployeeUtils;
import com.branches.utils.RepairEmployeeUtils;
import com.branches.utils.RepairUtils;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class RepairEmployeeServiceTest {
    @InjectMocks
    private RepairEmployeeService service;
    @Mock
    private RepairEmployeeRepository repository;
    @Mock
    private RepairService repairService;
    @Mock
    private EmployeeService employeeService;
    @Mock
    private RepairEmployeeMapper mapper;
    private List<RepairEmployee> repairEmployeeList;

    @BeforeEach
    void init() {
        repairEmployeeList = RepairEmployeeUtils.newRepairEmployeeList();
    }

    @Test
    @DisplayName("findAllByRepairId returns all repairEmployees from given repair id when successful")
    @Order(1)
    void findAllByRepairId_ReturnsAllRepairEmployeesFromGivenRepairId_WhenSuccessful() {
        Repair repair = RepairUtils.newRepairList().getFirst();
        Long repairId = repair.getId();

        RepairEmployee repairEmployee = repairEmployeeList.getFirst();
        List<RepairEmployee> foundRepairEmployees = List.of(repairEmployee);
        List<RepairEmployeePostResponse> expectedResponse = List.of(RepairEmployeeUtils.newRepairEmployeeByRepairGetEmployees());

        BDDMockito.when(repairService.findByIdOrThrowsNotFoundException(repairId)).thenReturn(repair);
        BDDMockito.when(repository.findAllByRepair(repair)).thenReturn(foundRepairEmployees);
        BDDMockito.when(mapper.toRepairEmployeePostResponseList(foundRepairEmployees)).thenReturn(expectedResponse);

        List<RepairEmployeePostResponse> response = service.findAllByRepairId(repairId);

        Assertions.assertThat(response)
                .isNotNull()
                .isNotEmpty()
                .containsExactlyElementsOf(expectedResponse);
    }

    @Test
    @DisplayName("findAllByRepairId returns an empty list when when repair contain no employees")
    @Order(2)
    void findAllByRepairId_ReturnsEmptyList_WhenRepairContainNoEmployees() {
        Repair repair = RepairUtils.newRepairList().getLast();
        Long repairId = repair.getId();

        BDDMockito.when(repairService.findByIdOrThrowsNotFoundException(repairId)).thenReturn(repair);
        BDDMockito.when(repository.findAllByRepair(repair)).thenReturn(Collections.emptyList());
        BDDMockito.when(mapper.toRepairEmployeePostResponseList(Collections.emptyList())).thenReturn(Collections.emptyList());

        List<RepairEmployeePostResponse> response = service.findAllByRepairId(repairId);

        Assertions.assertThat(response)
                .isNotNull()
                .isEmpty();
    }

    @Test
    @DisplayName("findAllByRepairId throws NotFoundException when given repair id is not found")
    @Order(3)
    void findAllByRepairId_ThrowsNotFoundException_WhenGivenRepairIdIsNotFound() {
        Long randomRepairId = 121123L;

        BDDMockito.when(repairService.findByIdOrThrowsNotFoundException(randomRepairId)).thenThrow(NotFoundException.class);

        Assertions.assertThatThrownBy(() -> service.findAllByRepairId(randomRepairId))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    @DisplayName("findByRepairAndEmployeeOrThrowsNotFoundException returns found repairEmployee when successful")
    @Order(4)
    void findByRepairAndEmployeeOrThrowsNotFoundException_ReturnsFoundRepairEmployee_WhenSuccessful() {
        Repair repair = RepairUtils.newRepairList().getFirst();
        Employee employee = EmployeeUtils.newEmployeeList().getFirst();

        RepairEmployee expectedResponse = repairEmployeeList.getFirst();

        BDDMockito.when(repository.findByRepair_IdAndEmployee_Id(repair.getId(), employee.getId())).thenReturn(Optional.of(expectedResponse));

        RepairEmployee response = service.findByRepairAndEmployeeOrThrowsNotFoundException(repair, employee);

        Assertions.assertThat(response)
                .isNotNull()
                .isEqualTo(expectedResponse);
    }

    @Test
    @DisplayName("findByRepairAndEmployeeOrThrowsNotFoundException throws NotFoundException when employee is not found in repair")
    @Order(5)
    void findByRepairAndEmployeeOrThrowsNotFoundException_ThrowsNotFoundException_WhenEmployeeIsNotFoundInRepair() {
        Repair repair = RepairUtils.newRepairList().getFirst();
        Employee employee = EmployeeUtils.newEmployeeList().getLast();

        BDDMockito.when(repository.findByRepair_IdAndEmployee_Id(repair.getId(), employee.getId())).thenReturn(Optional.empty());

        Assertions.assertThatThrownBy(() -> service.findByRepairAndEmployeeOrThrowsNotFoundException(repair, employee))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("The employee was not found in the repair");
    }

    @Test
    @DisplayName("save returns saved repairEmployee when successful")
    @Order(6)
    void save_ReturnsSavedRepairEmployee_WhenSuccessful() {
        Repair repair = RepairUtils.newRepairList().getFirst();
        Long repairId = repair.getId();

        RepairEmployeePostRequest postRequest = RepairEmployeeUtils.newRepairEmployeePostRequest();
        RepairEmployeePostResponse postResponse = RepairEmployeeUtils.newRepairEmployeePostResponse();

        RepairEmployee repairEmployeeToSave = RepairEmployeeUtils.newRepairEmployeeToSave();
        Employee employee = repairEmployeeToSave.getEmployee();
        Long employeeId = employee.getId();
        RepairEmployee savedRepairEmployee = RepairEmployeeUtils.newRepairEmployeeSaved();


        BDDMockito.when(repairService.findByIdOrThrowsNotFoundException(repairId)).thenReturn(repair);
        BDDMockito.when(employeeService.findByIdOrThrowsNotFoundException(employeeId)).thenReturn(employee);
        BDDMockito.when(repository.findByRepair_IdAndEmployee_Id(repairId, employeeId)).thenReturn(Optional.empty());
        BDDMockito.when(repository.save(repairEmployeeToSave)).thenReturn(savedRepairEmployee);
        BDDMockito.doNothing().when(repairService).updateTotalValue(repairId, savedRepairEmployee.getTotalValue());
        BDDMockito.when(mapper.toRepairEmployeePostResponse(savedRepairEmployee)).thenReturn(postResponse);

        RepairEmployeePostResponse response = service.save(repairId, postRequest);

        Assertions.assertThat(response)
                .isNotNull()
                .isEqualTo(postResponse);
    }

    @Test
    @DisplayName("save updates repairEmployee when the given repair already contains the given employee")
    @Order(7)
    void save_UpdatesRepairEmployee_WhenTheGivenRepairAlreadyContainsTheGivenEmployee() {
        RepairEmployee repairEmployeeNotUpdated = repairEmployeeList.getFirst();

        Repair repair = repairEmployeeNotUpdated.getRepair();
        Long repairId = repair.getId();

        RepairEmployeePostRequest postRequest = RepairEmployeeUtils.newRepairEmployeePostRequest();
        int totalHoursWorked = postRequest.getHoursWorked() + repairEmployeeNotUpdated.getHoursWorked();
        double totalValue = repairEmployeeNotUpdated.getEmployee().getCategory().getHourlyPrice() * totalHoursWorked;
        RepairEmployeePostResponse postResponse = RepairEmployeeUtils.newRepairEmployeePostResponse().withHoursWorked(totalHoursWorked).withTotalValue(totalValue);

        RepairEmployee repairEmployeeToSave = RepairEmployeeUtils.newRepairEmployeeToSave().withId(repairEmployeeNotUpdated.getId()).withHoursWorked(totalHoursWorked).withTotalValue(totalValue);
        Employee employee = repairEmployeeToSave.getEmployee();
        Long employeeId = employee.getId();
        RepairEmployee savedRepairEmployee = RepairEmployeeUtils.newRepairEmployeeSaved();


        BDDMockito.when(repairService.findByIdOrThrowsNotFoundException(repairId)).thenReturn(repair);
        BDDMockito.when(employeeService.findByIdOrThrowsNotFoundException(employeeId)).thenReturn(employee);
        BDDMockito.when(repository.findByRepair_IdAndEmployee_Id(repairId, employeeId)).thenReturn(Optional.of(repairEmployeeNotUpdated));
        BDDMockito.when(repository.save(repairEmployeeToSave)).thenReturn(savedRepairEmployee);
        BDDMockito.doNothing().when(repairService).updateTotalValue(repairId, savedRepairEmployee.getTotalValue());
        BDDMockito.when(mapper.toRepairEmployeePostResponse(savedRepairEmployee)).thenReturn(postResponse);

        RepairEmployeePostResponse response = service.save(repairId, postRequest);

        Assertions.assertThat(response)
                .isNotNull()
                .isEqualTo(postResponse);
    }

    @Test
    @DisplayName("save throws NotFoundException when the given repair id is not found")
    @Order(8)
    void save_ThrowsNotFoundException_WhenTheGivenRepairIdIsNotFound() {
        Long randomRepairId = 999L;

        RepairEmployeePostRequest postRequest = RepairEmployeeUtils.newRepairEmployeePostRequest();

        BDDMockito.when(repairService.findByIdOrThrowsNotFoundException(randomRepairId)).thenThrow(NotFoundException.class);

        Assertions.assertThatThrownBy(() -> service.save(randomRepairId, postRequest))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    @DisplayName("save throws BadRequestException when the given employee id is not found")
    @Order(9)
    void save_ThrowsBadRequestException_WhenSomeGivenEmployeeIsNotFound() {
        Repair repair = RepairUtils.newRepairList().getFirst();
        Long repairId = repair.getId();

        long randomEmployeeId = 999L;

        RepairEmployeePostRequest postRequest = RepairEmployeeUtils.newRepairEmployeePostRequest().withEmployeeId(randomEmployeeId);

        BDDMockito.when(repairService.findByIdOrThrowsNotFoundException(repairId)).thenReturn(repair);
        BDDMockito.when(employeeService.findByIdOrThrowsNotFoundException(randomEmployeeId)).thenThrow(NotFoundException.class);

        Assertions.assertThatThrownBy(() -> service.save(repairId, postRequest))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    @DisplayName("deleteByRepairIdAndEmployeeId removes repairEmployee when successful")
    @Order(10)
    void deleteByRepairAndEmployee_RemovesRepairIdEmployee_Id_WhenSuccessful() {
        RepairEmployee repairEmployeeToDelete = repairEmployeeList.getFirst();

        Repair repair = repairEmployeeToDelete.getRepair();
        Long repairId = repair.getId();
        Employee employee = repairEmployeeToDelete.getEmployee();
        Long employeeId = employee.getId();

        BDDMockito.when(repairService.findByIdOrThrowsNotFoundException(repairId)).thenReturn(repair);
        BDDMockito.when(employeeService.findByIdOrThrowsNotFoundException(employeeId)).thenReturn(employee);
        BDDMockito.when(repository.findByRepair_IdAndEmployee_Id(repairId, employeeId)).thenReturn(Optional.of(repairEmployeeToDelete));
        BDDMockito.doNothing().when(repository).deleteById(repairEmployeeToDelete.getId());
        BDDMockito.doNothing().when(repairService).updateTotalValue(repairId, -repairEmployeeToDelete.getTotalValue());

        Assertions.assertThatNoException()
                .isThrownBy(() -> service.deleteByRepairIdAndEmployeeId(repairId, employeeId));
    }

    @Test
    @DisplayName("deleteByRepairIdAndEmployeeId throws NotFoundException when the given repair id is not found")
    @Order(11)
    void deleteByRepairIdAndEmployeeId_ThrowsNotFoundException_WhenRepairIsNotFound() {
        Long randomRepairId = 5514121L;

        Employee employee = EmployeeUtils.newEmployeeList().getFirst();
        Long employeeId = employee.getId();

        BDDMockito.when(repairService.findByIdOrThrowsNotFoundException(randomRepairId)).thenThrow(NotFoundException.class);

        Assertions.assertThatThrownBy(() -> service.deleteByRepairIdAndEmployeeId(randomRepairId, employeeId))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    @DisplayName("deleteByRepairIdAndEmployeeId throws NotFoundException when the given employee id is not found")
    @Order(12)
    void deleteByRepairIdAndEmployeeId_ThrowsNotFoundException_WhenEmployeeIsNotFound() {
        Repair repair = RepairUtils.newRepairList().getFirst();
        Long repairId = repair.getId();

        Long randomEmployeeId = 5514121L;

        BDDMockito.when(repairService.findByIdOrThrowsNotFoundException(repairId)).thenReturn(repair);
        BDDMockito.when(employeeService.findByIdOrThrowsNotFoundException(randomEmployeeId)).thenThrow(NotFoundException.class);

        Assertions.assertThatThrownBy(() -> service.deleteByRepairIdAndEmployeeId(repairId, randomEmployeeId))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    @DisplayName("deleteByRepairIdAndEmployeeId throws NotFoundException when employee is not found in the repair")
    @Order(13)
    void deleteByRepairIdAndEmployeeId_ThrowsNotFoundException_WhenEmployeeIsNotFoundInTheRepair() {
        Repair repair = RepairUtils.newRepairList().getFirst();
        Long repairId = repair.getId();

        Employee employee = EmployeeUtils.newEmployeeList().getLast();
        Long employeeId = employee.getId();

        BDDMockito.when(repairService.findByIdOrThrowsNotFoundException(repairId)).thenReturn(repair);
        BDDMockito.when(employeeService.findByIdOrThrowsNotFoundException(employeeId)).thenReturn(employee);
        BDDMockito.when(repository.findByRepair_IdAndEmployee_Id(repairId, employeeId)).thenReturn(Optional.empty());

        Assertions.assertThatCode(() -> service.deleteByRepairIdAndEmployeeId(repairId, employeeId))
                .isInstanceOf(NotFoundException.class);
    }
}