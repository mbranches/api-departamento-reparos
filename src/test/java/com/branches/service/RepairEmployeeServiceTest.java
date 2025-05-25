package com.branches.service;

import com.branches.exception.NotFoundException;
import com.branches.model.Employee;
import com.branches.model.Repair;
import com.branches.model.RepairEmployee;
import com.branches.repository.RepairEmployeeRepository;
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
//    @InjectMocks
//    private RepairEmployeeService service;
//    @Mock
//    private RepairEmployeeRepository repository;
//
//    @Test
//    @DisplayName("saveAll returns saved RepairEmployees when successful")
//    @Order(1)
//    void saveAll_ReturnsSavedRepairEmployees_WhenSuccessful() {
//        RepairEmployee repairEmployeeToSave = RepairEmployeeUtils.newRepairEmployeeToSave();
//        RepairEmployee repairEmployeeSaved = RepairEmployeeUtils.newRepairEmployeeSaved();
//
//        List<RepairEmployee> repairEmployeeToSaveList = List.of(repairEmployeeToSave);
//        List<RepairEmployee> repairEmployeeSavedList = List.of(repairEmployeeSaved);
//
//        BDDMockito.when(repository.saveAll(repairEmployeeToSaveList)).thenReturn(repairEmployeeSavedList);
//
//        List<RepairEmployee> response = service.saveAll(repairEmployeeToSaveList);
//
//        Assertions.assertThat(response)
//                .isNotNull()
//                .isNotEmpty()
//                .containsExactlyElementsOf(repairEmployeeSavedList);
//    }
//
//    @Test
//    @DisplayName("findAllByRepair returns all repair employees from given repair when successful")
//    @Order(2)
//    void findAllByRepair_ReturnsAllRepairEmployeesFromGivenRepair_WhenSuccessful() {
//        Repair repairToSearch = RepairUtils.newRepairList().getFirst();
//        RepairEmployee repairEmployee = RepairEmployeeUtils.newRepairEmployeeList().getFirst();
//        List<RepairEmployee> expectedResponse = List.of(repairEmployee);
//
//        BDDMockito.when(repository.findAllByRepair(repairToSearch)).thenReturn(expectedResponse);
//
//        List<RepairEmployee> response = service.findAllByRepair(repairToSearch);
//
//        Assertions.assertThat(response)
//                .isNotNull()
//                .isNotEmpty()
//                .containsExactlyElementsOf(expectedResponse);
//    }
//
//    @Test
//    @DisplayName("findAllByRepair returns an empty list when when given repair contain no employees")
//    @Order(3)
//    void findAllByRepair_ReturnsEmptyList_WhenGivenRepairContainNoEmployees() {
//        Repair repairToSearch = RepairUtils.newRepairList().getLast();
//
//        BDDMockito.when(repository.findAllByRepair(repairToSearch)).thenReturn(Collections.emptyList());
//
//        List<RepairEmployee> response = service.findAllByRepair(repairToSearch);
//
//        Assertions.assertThat(response)
//                .isNotNull()
//                .isEmpty();
//    }
//
//    @Test
//    @DisplayName("findByRepairAndEmployeeOrThrowsNotFoundException returns found RepairEmployee when successful")
//    @Order(4)
//    void findByRepairAndEmployeeOrThrowsNotFoundException_ReturnsFoundRepairEmployee_WhenSuccessful() {
//        Repair repair = RepairUtils.newRepairList().getFirst();
//        Employee employee = EmployeeUtils.newEmployeeList().getFirst();
//
//        RepairEmployee expectedResponse = RepairEmployeeUtils.newRepairEmployeeList().getFirst();
//
//        BDDMockito.when(repository.findByRepairAndEmployee(repair, employee)).thenReturn(Optional.of(expectedResponse));
//
//        RepairEmployee response = service.findByRepairAndEmployeeOrThrowsNotFoundException(repair, employee);
//
//        Assertions.assertThat(response)
//                .isNotNull()
//                .isEqualTo(expectedResponse);
//    }
//
//    @Test
//    @DisplayName("findByRepairAndEmployeeOrThrowsNotFoundException throws NotFoundException when employee is not found in repair")
//    @Order(5)
//    void findByRepairAndEmployeeOrThrowsNotFoundException_ThrowsNotFoundException_WhenEmployeeIsNotFoundInRepair() {
//        Repair repair = RepairUtils.newRepairList().getFirst();
//        Employee employee = EmployeeUtils.newEmployeeList().getLast();
//
//        BDDMockito.when(repository.findByRepairAndEmployee(repair, employee)).thenReturn(Optional.empty());
//
//        Assertions.assertThatThrownBy(() -> service.findByRepairAndEmployeeOrThrowsNotFoundException(repair, employee))
//                .isInstanceOf(NotFoundException.class)
//                .hasMessageContaining("The employee was not found in the repair");
//    }
//
//    @Test
//    @DisplayName("deleteByRepairIdAndEmployeeId removes repair employee when successful")
//    @Order(6)
//    void deleteByRepairAndEmployee_RemovesRepairIdEmployee_Id_WhenSuccessful() {
//        Repair repair = RepairUtils.newRepairList().getFirst();
//        Employee employee = EmployeeUtils.newEmployeeList().getFirst();
//
//        RepairEmployee repairEmployee = RepairEmployeeUtils.newRepairEmployeeList().getFirst();
//
//        BDDMockito.when(repository.findByRepairAndEmployee(repair, employee)).thenReturn(Optional.of(repairEmployee));
//        BDDMockito.doNothing().when(repository).delete(repairEmployee);
//
//        Assertions.assertThatCode(() -> service.deleteByRepairIdAndEmployeeId(repair, employee))
//                .doesNotThrowAnyException();
//    }
//
//    @Test
//    @DisplayName("deleteByRepairIdAndEmployeeId throws NotFoundException when employee is not found in the repair")
//    @Order(7)
//    void deleteByRepairAndEmployee_ThrowsNotFoundException_WhenEmployeeIsNotFoundInTheRepairId() {
//        Repair repair = RepairUtils.newRepairList().getFirst();
//        Employee employee = EmployeeUtils.newEmployeeList().getLast();
//
//        BDDMockito.when(repository.findByRepairAndEmployee(repair, employee)).thenReturn(Optional.empty());
//
//        Assertions.assertThatThrownBy(() -> service.deleteByRepairIdAndEmployeeId(repair, employee))
//                .isInstanceOf(NotFoundException.class)
//                .hasMessageContaining("The employee was not found in the repair");
//    }
}