package com.branches.service;

import com.branches.exception.NotFoundException;
import com.branches.mapper.VehicleMapper;
import com.branches.model.Vehicle;
import com.branches.repository.VehicleRepository;
import com.branches.request.VehiclePostRequest;
import com.branches.response.VehiclePostResponse;
import com.branches.response.VehicleGetResponse;
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

import java.util.List;

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
    @DisplayName("save returns saved vehicle when successful")
    @Order(2)
    void save_ReturnsSavedVehicle_WhenGivenAddressExists() {
        Vehicle vehicleToSave = VehicleUtils.newVehicleToSave();
        VehiclePostRequest vehiclePostRequest = VehicleUtils.newVehiclePostRequest();

        VehiclePostResponse expectedResponse = VehicleUtils.newVehiclePostResponse();

        BDDMockito.when(clientService.findByIdOrElseThrowsNotFoundException(vehiclePostRequest.getClientId())).thenReturn(ClientUtils.newClientToSave());
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
    @Order(3)
    void save_ThrowsNotFoundException_WhenGivenCategoryNotExists() {
        VehiclePostRequest vehiclePostRequest = VehicleUtils.newVehiclePostRequest();

        BDDMockito.when(clientService.findByIdOrElseThrowsNotFoundException(vehiclePostRequest.getClientId())).thenThrow(NotFoundException.class);

        Assertions.assertThatThrownBy(() -> service.save(vehiclePostRequest))
                .isInstanceOf(NotFoundException.class);
    }
}