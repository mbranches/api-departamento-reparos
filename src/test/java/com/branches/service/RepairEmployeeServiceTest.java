package com.branches.service;

import com.branches.model.Repair;
import com.branches.model.RepairEmployee;
import com.branches.repository.RepairEmployeeRepository;
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

@ExtendWith(MockitoExtension.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class RepairEmployeeServiceTest {
    @InjectMocks
    private RepairEmployeeService service;
    @Mock
    private RepairEmployeeRepository repository;

    @Test
    @DisplayName("saveAll returns saved RepairEmployees when successful")
    @Order(1)
    void saveAll_ReturnsSavedRepairEmployees_WhenSuccessful() {
        RepairEmployee repairEmployeeToSave = RepairEmployeeUtils.newRepairEmployee();

        List<RepairEmployee> repairEmployeesToSave = List.of(repairEmployeeToSave);

        BDDMockito.when(repository.saveAll(repairEmployeesToSave)).thenReturn(repairEmployeesToSave);

        List<RepairEmployee> response = service.saveAll(repairEmployeesToSave);

        Assertions.assertThat(response)
                .isNotNull()
                .isNotEmpty()
                .containsExactlyElementsOf(repairEmployeesToSave);
    }

    @Test
    @DisplayName("findAllByRepair returns all repair employees from given repair when successful")
    @Order(2)
    void findAllByRepair_ReturnsAllRepairEmployeesFromGiveRepair_WhenSuccessful() {
        Repair repairToSearch = RepairUtils.newRepairList().getFirst();
        List<RepairEmployee> expectedResponse = List.of(RepairEmployeeUtils.newRepairEmployeeSaved());

        BDDMockito.when(repository.findAllByRepair(repairToSearch)).thenReturn(expectedResponse);

        List<RepairEmployee> response = service.findAllByRepair(repairToSearch);

        Assertions.assertThat(response)
                .isNotNull()
                .isNotEmpty()
                .containsExactlyElementsOf(expectedResponse);
    }

    @Test
    @DisplayName("findAllByRepair returns an empty list when when given repair contain no employees")
    @Order(3)
    void findAllByRepair_ReturnsEmptyList_WhenGivenRepairDoesNotContainEmployees() {
        Repair repairToSearch = RepairUtils.newRepairList().getLast();

        BDDMockito.when(repository.findAllByRepair(repairToSearch)).thenReturn(Collections.emptyList());

        List<RepairEmployee> response = service.findAllByRepair(repairToSearch);

        Assertions.assertThat(response)
                .isNotNull()
                .isEmpty();
    }
}