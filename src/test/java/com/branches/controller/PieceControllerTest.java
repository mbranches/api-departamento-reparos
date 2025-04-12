package com.branches.controller;

import com.branches.exception.NotFoundException;
import com.branches.request.PiecePostRequest;
import com.branches.response.PieceGetResponse;
import com.branches.service.PieceService;
import com.branches.utils.PieceUtils;
import com.branches.utils.FileUtils;
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

import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

@WebMvcTest(PieceController.class)
@Import(FileUtils.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class PieceControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @MockitoBean
    private PieceService service;
    @Autowired
    private FileUtils fileUtils;
    private final String URL = "/v1/pieces";
    private List<PieceGetResponse> pieceGetResponseList;

    @BeforeEach
    void init() {
        pieceGetResponseList = PieceUtils.newPieceGetResponseList();
    }

    @Test
    @DisplayName("GET /v1/pieces returns all pieces when the given argument is null")
    @Order(1)
    void findAll_ReturnsAllPieces_WhenGivenArgumentIsNull() throws Exception {
        BDDMockito.when(service.findAll(null)).thenReturn(pieceGetResponseList);
        String expectedResponse = fileUtils.readResourceFile("piece/get-pieces-null-name-200.json");

        mockMvc.perform(MockMvcRequestBuilders.get(URL))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().json(expectedResponse));
    }

    @Test
    @DisplayName("GET /v1/pieces?name=Óleo%20de%20motor returns the found pieces when the argument is given")
    @Order(2)
    void findAll_ReturnsFoundPieces_WhenArgumentIsGiven() throws Exception {
        String nameToSearch = "Óleo de motor";
        BDDMockito.when(service.findAll(nameToSearch)).thenReturn(List.of(pieceGetResponseList.getFirst()));
        String expectedResponse = fileUtils.readResourceFile("piece/get-pieces-oleoDeMotor-name-200.json");

        mockMvc.perform(MockMvcRequestBuilders.get(URL).param("name", nameToSearch))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().json(expectedResponse));
    }

    @Test
    @DisplayName("GET /v1/pieces?name=nameNotRegistered returns an empty list when the given argument is not found")
    @Order(3)
    void findAll_ReturnsEmptyList_WhenGivenArgumentIsNotFound() throws Exception {
        String randomName = "nameNotRegistered";
        BDDMockito.when(service.findAll(randomName)).thenReturn(Collections.emptyList());
        String expectedResponse = fileUtils.readResourceFile("piece/get-pieces-nameNotRegistered-name-200.json");

        mockMvc.perform(MockMvcRequestBuilders.get(URL).param("name", randomName))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().json(expectedResponse));
    }

    @Test
    @DisplayName("GET /v1/pieces/1 returns found Piece when successful")
    @Order(4)
    void findById_ReturnsFoundPiece_WhenSuccessful() throws Exception {
        PieceGetResponse expectedPiece = pieceGetResponseList.getFirst();
        long idToSearch = 1L;

        BDDMockito.when(service.findById(idToSearch)).thenReturn(expectedPiece);
        String expectedResponse = fileUtils.readResourceFile("piece/get-piece-by-id-200.json");

        mockMvc.perform(MockMvcRequestBuilders.get(URL + "/{id}", idToSearch))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().json(expectedResponse));
    }

    @Test
    @DisplayName("GET /v1/pieces/131222 throws NotFoundException when id is not found")
    @Order(5)
    void findById_ThrowsNotFoundException_WhenIdIsNotFound() throws Exception {
        long randomId = 131222L;

        BDDMockito.when(service.findById(randomId)).thenThrow(new NotFoundException("Piece not Found"));
        String expectedResponse = fileUtils.readResourceFile("piece/get-piece-by-id-404.json");

        mockMvc.perform(MockMvcRequestBuilders.get(URL + "/{id}", randomId))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect(MockMvcResultMatchers.content().json(expectedResponse));
    }

    @Test
    @DisplayName("POST /v1/pieces returns saved piece when successful")
    @Order(6)
    void save_ReturnsSavedPiece_WhenGivenAddressExists() throws Exception {
        BDDMockito.when(service.save(ArgumentMatchers.any(PiecePostRequest.class))).thenReturn(PieceUtils.newPiecePostResponse());
        String request = fileUtils.readResourceFile("piece/post-request-piece-200.json");
        String expectedResponse = fileUtils.readResourceFile("piece/post-response-piece-201.json");

        mockMvc.perform(
                        MockMvcRequestBuilders.post(URL)
                                .content(request)
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.content().json(expectedResponse));
    }

    @ParameterizedTest
    @MethodSource("postPieceBadRequestSource")
    @DisplayName("POST /v1/pieces return BadRequest when fields are invalid")
    @Order(7)
    void save_ReturnsBadRequest_WhenFieldAreInvalid(String fileName, List<String> expectedErrors) throws Exception {
        String request = fileUtils.readResourceFile("piece/%s".formatted(fileName));

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

    private static Stream<Arguments> postPieceBadRequestSource() {
        String nameRequiredError = "The field name is required";
        String unitValueRequiredError = "The field unitValue is required";

        List<String> expectedErrors = List.of(nameRequiredError, unitValueRequiredError);
        return Stream.of(
                Arguments.of("post-request-piece-empty-fields-400.json", expectedErrors),
                Arguments.of("post-request-piece-blank-fields-400.json", expectedErrors)
        );
    }
}
