package com.branches.controller;

import com.branches.exception.NotFoundException;
import com.branches.request.VehiclePostRequest;
import com.branches.response.VehicleGetResponse;
import com.branches.service.VehicleService;
import com.branches.utils.FileUtils;
import com.branches.utils.VehicleUtils;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.ArgumentMatchers;
import org.mockito.BDDMockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.List;
import java.util.stream.Stream;

@WebMvcTest(controllers = VehicleController.class)
@Import(FileUtils.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class VehicleControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @MockitoBean
    private VehicleService service;
    private final String URL = "/v1/vehicles";
    private List<VehicleGetResponse> vehicleGetResponseList;
    @Autowired
    private FileUtils fileUtils;

    @BeforeEach
    void init() {
        vehicleGetResponseList = VehicleUtils.newVehicleGetResponseList();
    }

    @Test
    @DisplayName("GET /v1/vehicles returns all vehicles when successful")
    @Order(1)
    void findAll_ReturnsAllVehicles_WhenSuccessful() throws Exception {
        BDDMockito.when(service.findAll()).thenReturn(vehicleGetResponseList);
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
        BDDMockito.when(service.save(ArgumentMatchers.any(VehiclePostRequest.class))).thenReturn(VehicleUtils.newVehiclePostResponse());

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
        BDDMockito.when(service.save(ArgumentMatchers.any(VehiclePostRequest.class))).thenThrow(new NotFoundException("Client not Found"));

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

    @ParameterizedTest
    @MethodSource("postVehicleBadRequestSource")
    @DisplayName("save return BadRequest when fields are invalid")
    @Order(4)
    void save_ReturnsBadRequest_WhenFieldAreInvalid(String fileName, List<String> expectedErrors) throws Exception {
        String request = fileUtils.readResourceFile("vehicle/%s".formatted(fileName));

        MvcResult mvcResult = mockMvc.perform(
                        MockMvcRequestBuilders.post(URL)
                                .content(request)
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andReturn();

        Exception exception = mvcResult.getResolvedException();

        Assertions.assertThat(exception.getMessage())
                .isNotNull()
                .contains(expectedErrors);
    }

    private static Stream<Arguments> postVehicleBadRequestSource() {
        String vehicleTypeRequiredError = "The field vehicleType is required";
        String brandRequiredError = "The field brand is required";
        String modelRequiredError = "The field model is required";
        String clientIdRequiredError = "The field clientId is required";

        List<String> expectedErrors = List.of(vehicleTypeRequiredError, brandRequiredError, modelRequiredError, clientIdRequiredError);
        return Stream.of(
                Arguments.of("post-request-vehicle-empty-fields-400.json", expectedErrors),
                Arguments.of("post-request-vehicle-blank-fields-400.json", expectedErrors)
        );
    }
}