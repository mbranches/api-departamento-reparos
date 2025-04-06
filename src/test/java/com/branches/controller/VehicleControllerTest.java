package com.branches.controller;

import com.branches.exception.NotFoundException;
import com.branches.mapper.VehicleMapperImpl;
import com.branches.model.Vehicle;
import com.branches.repository.VehicleRepository;
import com.branches.service.ClientService;
import com.branches.service.VehicleService;
import com.branches.utils.ClientUtils;
import com.branches.utils.FileUtils;
import com.branches.utils.VehicleUtils;
import org.junit.jupiter.api.*;
import org.mockito.ArgumentMatchers;
import org.mockito.BDDMockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.List;

@WebMvcTest(controllers = VehicleController.class)
@Import({VehicleService.class, VehicleMapperImpl.class, FileUtils.class})
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class VehicleControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @MockitoBean
    private VehicleRepository repository;
    @MockitoBean
    private ClientService clientService;
    private final String URL = "/v1/vehicles";
    private List<Vehicle> vehicleList;
    @Autowired
    private FileUtils fileUtils;

    @BeforeEach
    void init() {
        vehicleList = VehicleUtils.newVehicleList();
    }

    @Test
    @DisplayName("GET /v1/vehicles returns all vehicles when the given argument is null")
    @Order(1)
    void findAll_ReturnsAllVehicles_WhenGivenArgumentIsNull() throws Exception {
        BDDMockito.when(repository.findAll()).thenReturn(vehicleList);
        String expectedResponse = fileUtils.readResourceFile("vehicle/get-vehicles-200.json");

        mockMvc.perform(MockMvcRequestBuilders.get(URL))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().json(expectedResponse));
    }

    @Test
    @DisplayName("POST /v1/vehicles returns saved vehicle when successful")
    @Order(2)
    void save_ReturnsSavedVehicle_WhenSuccessful() throws Exception {
        Vehicle vehicleToSave = VehicleUtils.newVehicleToSave();
        vehicleToSave.setId(null);
        Vehicle vehicleSaved = VehicleUtils.newVehicleToSave();

        BDDMockito.when(repository.save(vehicleToSave)).thenReturn(vehicleSaved);
        BDDMockito.when(clientService.findByIdOrElseThrowsNotFoundException(vehicleToSave.getClient().getId())).thenReturn(ClientUtils.newClientToSave());

        String request = fileUtils.readResourceFile("vehicle/post-request-vehicle-valid-client-200.json");
        String expectedResponse = fileUtils.readResourceFile("vehicle/post-response-vehicle-201.json");

        mockMvc.perform(
                    MockMvcRequestBuilders.post(URL)
                            .content(request)
                            .contentType(MediaType.APPLICATION_JSON_VALUE)
                )
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.content().json(expectedResponse));
    }

    @Test
    @DisplayName("POST /v1/vehicles throws not found exception when given client does not exists")
    @Order(3)
    void save_ThrowsNotFoundException_WhenGivenClientNotExists() throws Exception {
        BDDMockito.when(clientService.findByIdOrElseThrowsNotFoundException(ArgumentMatchers.anyLong())).thenThrow(new NotFoundException("Client not Found"));

        String request = fileUtils.readResourceFile("vehicle/post-request-vehicle-invalid-client-200.json");
        String expectedResponse = fileUtils.readResourceFile("vehicle/post-response-vehicle-404.json");

        mockMvc.perform(
                        MockMvcRequestBuilders.post(URL)
                                .content(request)
                                .contentType(MediaType.APPLICATION_JSON_VALUE)
                )
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect(MockMvcResultMatchers.content().json(expectedResponse));
    }
}