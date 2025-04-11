package com.branches.controller;

import com.branches.exception.BadRequestException;
import com.branches.exception.NotFoundException;
import com.branches.model.Piece;
import com.branches.model.Repair;
import com.branches.model.RepairEmployee;
import com.branches.model.RepairPiece;
import com.branches.request.RepairPostRequest;
import com.branches.response.RepairGetResponse;
import com.branches.service.RepairService;
import com.branches.utils.*;
import org.assertj.core.api.Assertions;
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

@WebMvcTest(controllers = RepairController.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@Import(FileUtils.class)
class RepairControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @MockitoBean
    private RepairService service;
    @Autowired
    private FileUtils fileUtils;
    private List<RepairGetResponse> repairGetResponseList;
    private final String URL = "/v1/repairs";

    @BeforeEach
    void init() {
        repairGetResponseList = RepairUtils.newRepairGetResponseList();
    }

    @Test
    @DisplayName("GET /v1/repairs returns all repairs when successful")
    void findAll_ReturnsAllRepairs_WhenSuccessful() throws Exception {
        BDDMockito.when(service.findAll()).thenReturn(repairGetResponseList);

        String expectedResponse = fileUtils.readResourceFile("repair/get-repairs-200.json");

        mockMvc.perform(MockMvcRequestBuilders.get(URL))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().json(expectedResponse));
    }

    @Test
    @DisplayName("POST /v1/repairs returns saved repair when successful")
    @Order(2)
    void save_ReturnsSavedRepair_WhenSuccessful() throws Exception {
        String request = fileUtils.readResourceFile("repair/post-request-repair-200.json");
        String expectedResponse = fileUtils.readResourceFile("repair/post-response-repair-201.json");

        BDDMockito.when(service.save(ArgumentMatchers.any(RepairPostRequest.class))).thenReturn(RepairUtils.newRepairPostResponse());

        mockMvc.perform(
                    MockMvcRequestBuilders.post(URL)
                            .content(request)
                            .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.content().json(expectedResponse));
    }

    @Test
    @DisplayName("POST /v1/repairs throws NotFoundException when client is not found")
    @Order(3)
    void save_ThrowsNotFoundException_WhenClientIsNotFound() throws Exception {
        String request = fileUtils.readResourceFile("repair/post-request-repair-invalid-client-200.json");
        String expectedResponse = fileUtils.readResourceFile("repair/post-response-repair-invalid-client-404.json");

        BDDMockito.when(service.save(ArgumentMatchers.any(RepairPostRequest.class))).thenThrow(new NotFoundException("Client not Found"));

        mockMvc.perform(
                        MockMvcRequestBuilders.post(URL)
                                .content(request)
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect(MockMvcResultMatchers.content().json(expectedResponse));
    }

    @Test
    @DisplayName("POST /v1/repairs throws NotFoundException when vehicle is not found")
    @Order(4)
    void save_ThrowsNotFoundException_WhenVehicleIsNotFound() throws Exception {
        String request = fileUtils.readResourceFile("repair/post-request-repair-invalid-vehicle-200.json");
        String expectedResponse = fileUtils.readResourceFile("repair/post-response-repair-invalid-vehicle-404.json");

        BDDMockito.when(service.save(ArgumentMatchers.any(RepairPostRequest.class))).thenThrow(new NotFoundException("Vehicle not Found"));

        mockMvc.perform(
                        MockMvcRequestBuilders.post(URL)
                                .content(request)
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect(MockMvcResultMatchers.content().json(expectedResponse));
    }

    @Test
    @DisplayName("POST /v1/repairs throws BadRequestException when some piece is not found")
    @Order(5)
    void save_ThrowsBadRequestException_WhenSomePieceIsNotFound() throws Exception {
        String request = fileUtils.readResourceFile("repair/post-request-repair-invalid-piece-200.json");
        String expectedResponse = fileUtils.readResourceFile("repair/post-response-repair-invalid-piece-400.json");

        BDDMockito.when(service.save(ArgumentMatchers.any(RepairPostRequest.class))).thenThrow(new BadRequestException("Error saving pieces"));

        mockMvc.perform(
                        MockMvcRequestBuilders.post(URL)
                                .content(request)
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().json(expectedResponse));
    }

    @Test
    @DisplayName("POST /v1/repairs throws BadRequestException when some employee is not found")
    @Order(6)
    void save_ThrowsBadRequestException_WhenSomeEmployeeIsNotFound() throws Exception {
        String request = fileUtils.readResourceFile("repair/post-request-repair-invalid-employee-200.json");
        String expectedResponse = fileUtils.readResourceFile("repair/post-response-repair-invalid-employee-400.json");

        BDDMockito.when(service.save(ArgumentMatchers.any(RepairPostRequest.class))).thenThrow(new BadRequestException("Error saving employees"));

        mockMvc.perform(
                        MockMvcRequestBuilders.post(URL)
                                .content(request)
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().json(expectedResponse));
    }

    @Test
    @DisplayName("POST /v1/repairs throws BadRequestException when quantity is greater than piece stock")
    @Order(7)
    void save_ThrowsBadRequestException_WhenQuantityIsGreaterThanPieceStock() throws Exception {
        Piece pieceToSave = PieceUtils.newPieceToSave();

        String request = fileUtils.readResourceFile("repair/post-request-repair-invalid-piece-quantity-200.json");
        String expectedResponse = fileUtils.readResourceFile("repair/post-response-repair-invalid-piece-quantity-400.json");

        BDDMockito.when(service.save(ArgumentMatchers.any(RepairPostRequest.class))).thenThrow(new BadRequestException("'" + pieceToSave.getName() + "' has insufficient stock." +
                " Available: " + pieceToSave.getStock() + ", Requested: " + 5000));

        mockMvc.perform(
                        MockMvcRequestBuilders.post(URL)
                                .content(request)
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().json(expectedResponse));
    }
}