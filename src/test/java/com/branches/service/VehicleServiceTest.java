package com.branches.service;

import com.branches.exception.NotFoundException;
import com.branches.mapper.VehicleMapper;
import com.branches.model.Client;
import com.branches.model.Vehicle;
import com.branches.repository.VehicleRepository;
import com.branches.request.VehiclePostRequest;
import com.branches.response.VehicleGetResponse;
import com.branches.response.VehicleDefaultResponse;
import com.branches.response.VehiclePostResponse;
import com.branches.utils.ClientUtils;
import com.branches.utils.VehicleUtils;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.BDDMockito;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class VehicleServiceTest {
    @InjectMocks
    private VehicleService service;
    @Mock
    private VehicleRepository repository;
    @Mock
    private VehicleMapper mapper;
    @Mock
    private ClientService clientService;
    private List<Vehicle> vehicleList;
    private List<VehicleGetResponse> vehicleGetResponseList;

    @BeforeEach
    void init() {
        vehicleList = VehicleUtils.newVehicleList();
        vehicleGetResponseList = VehicleUtils.newVehicleGetResponseList();
    }

    @Test
    @DisplayName("findAll returns all vehicles when the given argument is null")
    @Order(1)
    void findAll_ReturnsAllVehicles_WhenGivenArgumentIsNull() {
        BDDMockito.when(repository.findAll()).thenReturn(vehicleList);
        BDDMockito.when(mapper.toVehicleGetResponseList(ArgumentMatchers.anyList())).thenReturn(vehicleGetResponseList);

        List<VehicleGetResponse> response = service.findAll();

        Assertions.assertThat(response)
                .isNotNull()
                .isNotEmpty()
                .containsExactlyElementsOf(vehicleGetResponseList);
    }

    @Test
    @DisplayName("findById returns found vehicle when successful")
    @Order(2)
    void findById_ReturnsFoundVehicle_WhenSuccessful() {
        Vehicle expectedResponseRepository = vehicleList.getFirst();
        Long idToSearch = expectedResponseRepository.getId();

        VehicleGetResponse expectedResponse = vehicleGetResponseList.getFirst();

        BDDMockito.when(repository.findById(idToSearch)).thenReturn(Optional.of(expectedResponseRepository));
        BDDMockito.when(mapper.toVehicleGetResponse(expectedResponseRepository)).thenReturn(expectedResponse);

        VehicleGetResponse response = service.findById(idToSearch);

        Assertions.assertThat(response)
                .isNotNull()
                .isEqualTo(expectedResponse);
    }

    @Test
    @DisplayName("findById throws NotFoundException when id is not found")
    @Order(3)
    void findById_ThrowsNotFoundException_WhenIdIsNotFound() {
        Long randomId = 4445511L;

        BDDMockito.when(repository.findById(randomId)).thenReturn(Optional.empty());

        Assertions.assertThatThrownBy(() -> service.findById(randomId))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("Vehicle with id '%s' not Found".formatted(randomId));
    }

    @Test
    @DisplayName("findVehiclesByClientId Returns all client vehicles when successful")
    @Order(4)
    void findVehiclesByClientId_ReturnsAllClientVehicles_WhenSuccessful() {
        Client client = ClientUtils.newClientSaved();
        long clientId = client.getId();

        List<Vehicle> vehicles = VehicleUtils.newVehicleList();
        List<VehicleDefaultResponse> expectedResponse = VehicleUtils.newVehicleClientGetReponseList();

        BDDMockito.when(clientService.findByIdOrThrowsNotFoundException(clientId)).thenReturn(client);
        BDDMockito.when(repository.findAllByClient(client)).thenReturn(vehicles);
        BDDMockito.when(mapper.toVehicleClientGetResponseList(vehicles)).thenReturn(expectedResponse);

        List<VehicleDefaultResponse> response = service.findByClientId(clientId);

        Assertions.assertThat(response)
                .isNotNull()
                .isNotEmpty()
                .containsExactlyElementsOf(expectedResponse);
    }

    @Test
    @DisplayName("findVehiclesByClientId returns an empty list when client doesn't have vehicles")
    @Order(5)
    void findVehiclesByClientId_ReturnsEmptyList_WhenClientDoesNotHaveVehicles() {
        Client client = ClientUtils.newClientSaved();
        long clientId = client.getId();

        BDDMockito.when(clientService.findByIdOrThrowsNotFoundException(clientId)).thenReturn(client);
        BDDMockito.when(repository.findAllByClient(client)).thenReturn(Collections.emptyList());

        List<VehicleDefaultResponse> response = service.findByClientId(clientId);

        Assertions.assertThat(response)
                .isNotNull()
                .isEmpty();
    }

    @Test
    @DisplayName("findVehiclesByClientId throws NotFoundException when client is not found")
    @Order(6)
    void findVehiclesByClientId_ThrowsNotFoundException_WhenClientIsNotFound() {
        long randomClientId = 1234567L;

        BDDMockito.when(clientService.findByIdOrThrowsNotFoundException(randomClientId)).thenThrow(NotFoundException.class);

        Assertions.assertThatThrownBy(() -> service.findByClientId(randomClientId))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    @DisplayName("save returns saved vehicle when successful")
    @Order(7)
    void save_ReturnsSavedVehicle_WhenSuccessful() {
        Vehicle vehicleToSave = VehicleUtils.newVehicleToSave();
        VehiclePostRequest vehiclePostRequest = VehicleUtils.newVehiclePostRequest();

        VehiclePostResponse expectedResponse = VehicleUtils.newVehiclePostResponse();

        BDDMockito.when(clientService.findByIdOrThrowsNotFoundException(vehiclePostRequest.getClientId())).thenReturn(ClientUtils.newClientSaved());
        BDDMockito.when(mapper.toVehicle(vehiclePostRequest)).thenReturn(vehicleToSave);
        BDDMockito.when(repository.save(vehicleToSave)).thenReturn(vehicleToSave);
        BDDMockito.when(mapper.toVehiclePostResponse(vehicleToSave)).thenReturn(expectedResponse);

        VehiclePostResponse response = service.save(vehiclePostRequest);

        Assertions.assertThat(response)
                .isNotNull()
                .isEqualTo(expectedResponse);
    }

    @Test
    @DisplayName("save throws not found exception when given client does not exists")
    @Order(8)
    void save_ThrowsNotFoundException_WhenGivenClientNotExists() {
        VehiclePostRequest vehiclePostRequest = VehicleUtils.newVehiclePostRequest();

        BDDMockito.when(clientService.findByIdOrThrowsNotFoundException(ArgumentMatchers.anyLong())).thenThrow(NotFoundException.class);

        Assertions.assertThatThrownBy(() -> service.save(vehiclePostRequest))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    @DisplayName("deleteById removes vehicle when successful")
    @Order(9)
    void deleteById_RemovesVehicle_WhenSuccessful() {
        Vehicle vehicleToDelete = vehicleList.getFirst();
        Long idToDelete = vehicleToDelete.getId();

        BDDMockito.when(repository.findById(idToDelete)).thenReturn(Optional.of(vehicleToDelete));
        BDDMockito.doNothing().when(repository).delete(ArgumentMatchers.any(Vehicle.class));

        Assertions.assertThatCode(() -> service.deleteById(idToDelete))
                .doesNotThrowAnyException();
    }

    @Test
    @DisplayName("deleteById throws NotFoundException when given id is not found")
    @Order(10)
    void deleteById_ThrowsNotFoundException_WhenGivenIdIsNotFound() {
        Long randomId = 15512366L;

        BDDMockito.when(repository.findById(randomId)).thenReturn(Optional.empty());

        Assertions.assertThatThrownBy(() -> service.deleteById(randomId))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("Vehicle with id '%s' not Found".formatted(randomId));
    }
}