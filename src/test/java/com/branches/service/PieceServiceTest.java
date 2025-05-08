package com.branches.service;

import com.branches.exception.BadRequestException;
import com.branches.exception.NotFoundException;
import com.branches.mapper.PieceMapper;
import com.branches.model.Piece;
import com.branches.repository.PieceRepository;
import com.branches.request.PiecePostRequest;
import com.branches.response.PieceGetResponse;
import com.branches.response.PiecePostResponse;
import com.branches.utils.PieceUtils;
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
class PieceServiceTest {
    @InjectMocks
    private PieceService service;
    @Mock
    private PieceRepository repository;
    @Mock
    private PieceMapper mapper;
    private List<Piece> pieceList;
    private List<PieceGetResponse> pieceGetResponseList;

    @BeforeEach
    void init() {
        pieceList = PieceUtils.newPieceList();
        pieceGetResponseList = PieceUtils.newPieceGetResponseList();
    }

    @Test
    @DisplayName("findAll returns all pieces when the given argument is null")
    @Order(1)
    void findAll_ReturnsAllPieces_WhenGivenArgumentIsNull() {
        BDDMockito.when(repository.findAll()).thenReturn(pieceList);
        BDDMockito.when(mapper.toPieceGetResponseList(ArgumentMatchers.anyList())).thenReturn(pieceGetResponseList);

        List<PieceGetResponse> response = service.findAll(null);

        Assertions.assertThat(response)
                .isNotNull()
                .isNotEmpty()
                .containsExactlyElementsOf(pieceGetResponseList);
    }

    @Test
    @DisplayName("findAll returns the found pieces when the argument is given")
    @Order(2)
    void findAll_ReturnsFoundPieces_WhenArgumentIsGiven() {
        PieceGetResponse pieceToBeFound = pieceGetResponseList.getFirst();
        String nameToSearch = pieceToBeFound.getName();
        List<PieceGetResponse> expectedResponse = List.of(pieceToBeFound);

        List<Piece> expectedResponseRepository = List.of(pieceList.getFirst());

        BDDMockito.when(repository.findAllByNameContaining(nameToSearch)).thenReturn(expectedResponseRepository);
        BDDMockito.when(mapper.toPieceGetResponseList(ArgumentMatchers.anyList())).thenReturn(expectedResponse);

        List<PieceGetResponse> response = service.findAll(nameToSearch);

        Assertions.assertThat(response)
                .isNotNull()
                .isNotEmpty()
                .containsExactlyElementsOf(expectedResponse);
    }

    @Test
    @DisplayName("findAll returns an empty list when the given argument is not found")
    @Order(3)
    void findAll_ReturnsEmptyList_WhenGivenArgumentIsNotFound() {
        String randomName = "name invalid";

        BDDMockito.when(repository.findAllByNameContaining(randomName)).thenReturn(Collections.emptyList());
        BDDMockito.when(mapper.toPieceGetResponseList(ArgumentMatchers.anyList())).thenReturn(Collections.emptyList());

        List<PieceGetResponse> response = service.findAll(randomName);

        Assertions.assertThat(response)
                .isNotNull()
                .isEmpty();
    }

    @Test
    @DisplayName("findById returns found Piece when successful")
    @Order(4)
    void findById_ReturnsFoundPiece_WhenSuccessful() {
        Piece expectedResponseRepository = pieceList.getFirst();
        Long idToSearch = expectedResponseRepository.getId();

        PieceGetResponse expectedResponse = pieceGetResponseList.getFirst();

        BDDMockito.when(repository.findById(idToSearch)).thenReturn(Optional.of(expectedResponseRepository));
        BDDMockito.when(mapper.toPieceGetResponse(expectedResponseRepository)).thenReturn(expectedResponse);

        PieceGetResponse response = service.findById(idToSearch);

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
                .hasMessageContaining("Piece with id '%s' not Found".formatted(randomId));
    }

    @Test
    @DisplayName("save returns saved piece when successful")
    @Order(6)
    void save_ReturnsSavedPiece_WhenGivenAddressExists() {
        Piece PieceToSave = PieceUtils.newPieceToSave();
        PiecePostRequest PiecePostRequest = PieceUtils.newPiecePostRequest();

        PiecePostResponse expectedResponse = PieceUtils.newPiecePostResponse();

        BDDMockito.when(mapper.toPiece(PiecePostRequest)).thenReturn(PieceToSave);
        BDDMockito.when(repository.save(PieceToSave)).thenReturn(PieceToSave);
        BDDMockito.when(mapper.toPiecePostResponse(PieceToSave)).thenReturn(expectedResponse);

        PiecePostResponse response = service.save(PiecePostRequest);

        Assertions.assertThat(response)
                .isNotNull()
                .isEqualTo(expectedResponse);
    }

    @Test
    @DisplayName("removesStock returns the piece with the removed stock when successful")
    @Order(7)
    void removesStock_ReturnsPieceWithRemovedStock_WhenSuccessful() {
        Piece pieceToRemoveStock = pieceList.getFirst();
        int quantityToRemove = 5;

        Piece expectedResponse = pieceList.getFirst();
        expectedResponse.setStock(expectedResponse.getStock() - quantityToRemove);

        BDDMockito.when(repository.save(expectedResponse)).thenReturn(expectedResponse);

        Piece response = service.removesStock(pieceToRemoveStock, quantityToRemove);

        Assertions.assertThat(response)
                .isNotNull()
                .isEqualTo(expectedResponse);
    }

    @Test
    @DisplayName("removeStock throws BadRequestException when quantity is greater than stock")
    @Order(7)
    void removesStock_ThrowsBadRequestException_WhenQuantityIsGreaterThanStock() {
        Piece pieceToRemoveStock = pieceList.getFirst();
        int quantityToRemove = 555555;

        Piece expectedResponse = pieceList.getFirst();
        expectedResponse.setStock(expectedResponse.getStock() - quantityToRemove);

        Assertions.assertThatThrownBy(() -> service.removesStock(pieceToRemoveStock, quantityToRemove))
                .isInstanceOf(BadRequestException.class);
    }

    @Test
    @DisplayName("deleteById removes piece when successful")
    @Order(8)
    void deleteById_RemovesPiece_WhenSuccessful() {
        Piece pieceToDelete = pieceList.getFirst();
        Long idToDelete = pieceToDelete.getId();

        BDDMockito.when(repository.findById(idToDelete)).thenReturn(Optional.of(pieceToDelete));
        BDDMockito.doNothing().when(repository).delete(ArgumentMatchers.any(Piece.class));

        Assertions.assertThatCode(() -> service.deleteById(idToDelete))
                .doesNotThrowAnyException();
    }

    @Test
    @DisplayName("deleteById throws NotFoundException when given id is not found")
    @Order(9)
    void deleteById_ThrowsNotFoundException_WhenGivenIdIsNotFound() {
        Long randomId = 15512366L;

        BDDMockito.when(repository.findById(randomId)).thenReturn(Optional.empty());

        Assertions.assertThatThrownBy(() -> service.deleteById(randomId))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("Piece with id '%s' not Found".formatted(randomId));
    }
}