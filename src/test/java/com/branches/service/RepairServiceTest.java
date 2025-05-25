package com.branches.service;

import com.branches.exception.NotFoundException;
import com.branches.mapper.RepairEmployeeMapper;
import com.branches.mapper.RepairMapper;
import com.branches.mapper.RepairPieceMapper;
import com.branches.model.*;
import com.branches.repository.RepairRepository;
import com.branches.request.RepairPostRequest;
import com.branches.response.RepairGetResponse;
import com.branches.response.RepairPostResponse;
import com.branches.utils.*;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
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
    private ClientService clientService;
    @Mock
    private VehicleService vehicleService;
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
                .hasMessageContaining("Repair with id '%s' not Found".formatted(randomId));
    }

    @Test
    @DisplayName("findAllByClientId returns all client vehicles when successful")
    @Order(6)
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
    @Order(7)
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
    @Order(8)
    void findAllByClientId_ThrowsNotFoundException_WhenClientIsNotFound() {
        Long randomId = 1515151L;

        BDDMockito.when(clientService.findByIdOrThrowsNotFoundException(randomId)).thenThrow(NotFoundException.class);

        Assertions.assertThatThrownBy(() -> service.findAllByClientId(randomId))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    @DisplayName("save returns saved repair when successful")
    @Order(9)
    void save_ReturnsSavedRepair_WhenSuccessful() {
        RepairPostRequest postRequest = RepairUtils.newRepairPostRequest();
        RepairPostResponse postResponse = RepairUtils.newRepairPostResponse();

        Repair repairToSave = RepairUtils.newRepairToSave();
        Repair savedRepair = RepairUtils.newRepairSaved();

        Client client = ClientUtils.newClientList().getFirst();
        Vehicle vehicle = VehicleUtils.newVehicleList().getFirst();
        BDDMockito.when(clientService.findByIdOrThrowsNotFoundException(postRequest.getClientId())).thenReturn(client);
        BDDMockito.when(vehicleService.findByIdOrThrowsNotFoundException(postRequest.getVehicleId())).thenReturn(vehicle);
        BDDMockito.when(repository.save(repairToSave)).thenReturn(savedRepair);
        BDDMockito.when(mapper.toRepairPostResponse(savedRepair)).thenReturn(postResponse);

        RepairPostResponse response = service.save(postRequest);

        Assertions.assertThat(response)
                .isNotNull()
                .isEqualTo(postResponse);
    }

    @Test
    @DisplayName("save throws NotFoundException when client is not found")
    @Order(10)
    void save_ThrowsNotFoundException_WhenClientIsNotFound() {
        RepairPostRequest postRequest = RepairUtils.newRepairPostRequest();

        BDDMockito.when(clientService.findByIdOrThrowsNotFoundException(postRequest.getClientId())).thenThrow(NotFoundException.class);

        Assertions.assertThatThrownBy(() -> service.save(postRequest))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    @DisplayName("save throws NotFoundException when vehicle is not found")
    @Order(11)
    void save_ThrowsNotFoundException_WhenVehicleIsNotFound() {
        RepairPostRequest postRequest = RepairUtils.newRepairPostRequest().withClientId(999L);

        BDDMockito.when(clientService.findByIdOrThrowsNotFoundException(postRequest.getClientId())).thenReturn(ClientUtils.newClientSaved());
        BDDMockito.when(vehicleService.findByIdOrThrowsNotFoundException(postRequest.getVehicleId())).thenThrow(NotFoundException.class);

        Assertions.assertThatThrownBy(() -> service.save(postRequest))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    @DisplayName("deleteById removes repair when successful")
    @Order(12)
    void deleteById_RemovesRepair_WhenSuccessful() {
        Repair repairToDelete = repairList.getFirst();
        Long idToDelete = repairToDelete.getId();

        BDDMockito.when(repository.findById(idToDelete)).thenReturn(Optional.of(repairToDelete));
        BDDMockito.doNothing().when(repository).delete(repairToDelete);

        Assertions.assertThatCode(() -> service.deleteById(idToDelete))
                .doesNotThrowAnyException();
    }

    @Test
    @DisplayName("deleteById throws NotFoundException when given id is not found")
    @Order(13)
    void deleteById_ThrowsNotFoundException_WhenGivenIdIsNotFound() {
        Long randomId = 15512366L;

        BDDMockito.when(repository.findById(randomId)).thenReturn(Optional.empty());

        Assertions.assertThatThrownBy(() -> service.deleteById(randomId))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("Repair with id '%s' not Found".formatted(randomId));
    }

    @Test
    @DisplayName("updateTotalValue updates total value when successful")
    @Order(14)
    void updateTotalValue_UpdatesRepairTotalValue_WhenSuccessful() {
        Repair repairToUpdateTotalValue = repairList.getFirst();
        Long repairId = repairToUpdateTotalValue.getId();
        double valueToSum = 1000D;
        Repair repairUpdated = repairToUpdateTotalValue.withTotalValue(repairToUpdateTotalValue.getTotalValue() + valueToSum);

        BDDMockito.when(repository.findById(repairId)).thenReturn(Optional.of(repairToUpdateTotalValue));
        BDDMockito.when(repository.save(repairUpdated)).thenReturn(repairUpdated);

        Assertions.assertThatNoException()
                .isThrownBy(() -> service.updateTotalValue(repairId, valueToSum));
    }

    @Test
    @DisplayName("updateTotalValue throws NotFoundException when the given repair id is not found")
    @Order(15)
    void updateTotalValue_ThrowsNotFoundException_WhenTheGivenRepairIdIsNotFound() {
        Long randomRepairId = 999L;
        double valueToSum = 1000D;

        BDDMockito.when(repository.findById(randomRepairId)).thenReturn(Optional.empty());

        Assertions.assertThatThrownBy(() -> service.updateTotalValue(randomRepairId, valueToSum))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("Repair with id '%s' not Found".formatted(randomRepairId));
    }
}