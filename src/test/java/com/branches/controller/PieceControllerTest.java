package com.branches.controller;

import com.branches.mapper.PieceMapperImpl;
import com.branches.model.Piece;
import com.branches.repository.PieceRepository;
import com.branches.service.PieceService;
import com.branches.utils.PieceUtils;
import com.branches.utils.FileUtils;
import org.junit.jupiter.api.*;
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

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@WebMvcTest(PieceController.class)
@Import({PieceService.class, PieceMapperImpl.class, FileUtils.class})
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class PieceControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @MockitoBean
    private PieceRepository repository;
    @Autowired
    private FileUtils fileUtils;
    private final String URL = "/v1/pieces";
    private List<Piece> pieceList;

    @BeforeEach
    void init() {
        pieceList = PieceUtils.newPieceList();
    }

    @Test
    @DisplayName("GET /v1/pieces returns all pieces when the given argument is null")
    @Order(1)
    void findAll_ReturnsAllPieces_WhenGivenArgumentIsNull() throws Exception {
        BDDMockito.when(repository.findAll()).thenReturn(pieceList);
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
        BDDMockito.when(repository.findAllByNameContaining(nameToSearch)).thenReturn(List.of(pieceList.getFirst()));
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
        BDDMockito.when(repository.findAllByNameContaining(randomName)).thenReturn(Collections.emptyList());
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
        Piece expectedPiece = pieceList.getFirst();
        long idToSearch = expectedPiece.getId();

        BDDMockito.when(repository.findById(idToSearch)).thenReturn(Optional.of(expectedPiece));
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

        BDDMockito.when(repository.findById(randomId)).thenReturn(Optional.empty());
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
        Piece pieceToSave = PieceUtils.newPieceToSave();
        pieceToSave.setId(null);
        Piece pieceSaved = PieceUtils.newPieceToSave();

        BDDMockito.when(repository.save(pieceToSave)).thenReturn(pieceSaved);
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
}
