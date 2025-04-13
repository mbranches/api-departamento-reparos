package com.branches.service;

import com.branches.exception.BadRequestException;
import com.branches.model.Piece;
import com.branches.model.Repair;
import com.branches.model.RepairPiece;
import com.branches.repository.RepairPieceRepository;
import com.branches.utils.RepairPieceUtils;
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
class RepairPieceServiceTest {
    @InjectMocks
    private RepairPieceService service;
    @Mock
    private RepairPieceRepository repository;
    @Mock
    private PieceService pieceService;

    @Test
    @DisplayName("findAllByRepair returns all repair pieces from given repair when successful")
    @Order(1)
    void findAllByRepair_ReturnsAllRepairPiecesFromGivenRepair_WhenSuccessful() {
        Repair repairToSearch = RepairUtils.newRepairList().getFirst();
        List<RepairPiece> expectedResponse = List.of(RepairPieceUtils.newRepairPieceSaved());

        BDDMockito.when(repository.findAllByRepair(repairToSearch)).thenReturn(expectedResponse);

        List<RepairPiece> response = service.findAllByRepair(repairToSearch);

        Assertions.assertThat(response)
                .isNotNull()
                .isNotEmpty()
                .containsExactlyElementsOf(expectedResponse);
    }

    @Test
    @DisplayName("findAllByRepair returns an empty list when when given repair contain no pieces")
    @Order(2)
    void findAllByRepair_ReturnsEmptyList_WhenGivenRepairContainNoPieces() {
        Repair repairToSearch = RepairUtils.newRepairList().getLast();

        BDDMockito.when(repository.findAllByRepair(repairToSearch)).thenReturn(Collections.emptyList());

        List<RepairPiece> response = service.findAllByRepair(repairToSearch);

        Assertions.assertThat(response)
                .isNotNull()
                .isEmpty();
    }


    @Test
    @DisplayName("saveAll returns saved RepairPieces when successful")
    @Order(3)
    void saveAll_ReturnsSavedRepairPieces_WhenSuccessful() {
        RepairPiece repairPieceToSave = RepairPieceUtils.newRepairPiece();
        List<RepairPiece> repairPieceList = List.of(repairPieceToSave);

        Piece pieceToRemoveStock = repairPieceToSave.getPiece();
        int quantityToRemove = repairPieceToSave.getQuantity();

        Piece expectedPiece = repairPieceToSave.getPiece();
        expectedPiece.setStock(expectedPiece.getStock() - quantityToRemove);

        RepairPiece expectedRepairPiece = RepairPieceUtils.newRepairPiece();
        expectedRepairPiece.setPiece(expectedPiece);

        BDDMockito.when(pieceService.removesStock(pieceToRemoveStock, quantityToRemove)).thenReturn(expectedPiece);
        BDDMockito.when(repository.save(expectedRepairPiece)).thenReturn(expectedRepairPiece);

        List<RepairPiece> expectedResponse = List.of(expectedRepairPiece);

        List<RepairPiece> response = service.saveAll(repairPieceList);

        Assertions.assertThat(response)
                .isNotNull()
                .isNotEmpty()
                .containsExactlyElementsOf(expectedResponse);
    }

    @Test
    @DisplayName("saveAll throws BadRequestException when quantity is greater than piece stock")
    @Order(4)
    void saveAll_ThrowsBadRequestException_WhenQuantityIsGreaterThanPieceStock() {
        RepairPiece repairPieceToSave = RepairPieceUtils.newRepairPiece();
        repairPieceToSave.setQuantity(212131);
        List<RepairPiece> repairPieceList = List.of(repairPieceToSave);

        Piece pieceToRemoveStock = repairPieceToSave.getPiece();
        int quantityToRemove = repairPieceToSave.getQuantity();

        BDDMockito.when(pieceService.removesStock(pieceToRemoveStock, quantityToRemove))
                .thenThrow(new BadRequestException("'" + pieceToRemoveStock.getName() + "' has insufficient stock." +
                " Available: " + pieceToRemoveStock.getStock() + ", Requested: " + repairPieceToSave.getQuantity()));

        Assertions.assertThatThrownBy(() -> service.saveAll(repairPieceList))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("'" + pieceToRemoveStock.getName() + "' has insufficient stock." +
                        " Available: " + pieceToRemoveStock.getStock() + ", Requested: " + repairPieceToSave.getQuantity());
    }

    @Test
    @DisplayName("save returns saved RepairPieces when successful")
    @Order(5)
    void save_ReturnsSavedRepairPieces_WhenSuccessful() {
        RepairPiece repairPieceToSave = RepairPieceUtils.newRepairPiece();

        Piece pieceToRemoveStock = repairPieceToSave.getPiece();
        int quantityToRemove = repairPieceToSave.getQuantity();

        Piece expectedPiece = repairPieceToSave.getPiece();
        expectedPiece.setStock(expectedPiece.getStock() - quantityToRemove);

        RepairPiece expectedRepairPiece = RepairPieceUtils.newRepairPiece();
        expectedRepairPiece.setPiece(expectedPiece);

        BDDMockito.when(pieceService.removesStock(pieceToRemoveStock, quantityToRemove)).thenReturn(expectedPiece);
        BDDMockito.when(repository.save(expectedRepairPiece)).thenReturn(expectedRepairPiece);

        RepairPiece expectedResponse = expectedRepairPiece;

        RepairPiece response = service.save(repairPieceToSave);

        Assertions.assertThat(response)
                .isNotNull()
                .isEqualTo(expectedResponse);
    }

    @Test
    @DisplayName("save throws BadRequestException when quantity is greater than piece stock")
    @Order(6)
    void save_ThrowsBadRequestException_WhenQuantityIsGreaterThanPieceStock() {
        RepairPiece repairPieceToSave = RepairPieceUtils.newRepairPiece();
        repairPieceToSave.setQuantity(212131);

        Piece pieceToRemoveStock = repairPieceToSave.getPiece();
        int quantityToRemove = repairPieceToSave.getQuantity();

        BDDMockito.when(pieceService.removesStock(pieceToRemoveStock, quantityToRemove))
                .thenThrow(new BadRequestException("'" + pieceToRemoveStock.getName() + "' has insufficient stock." +
                " Available: " + pieceToRemoveStock.getStock() + ", Requested: " + repairPieceToSave.getQuantity()));

        Assertions.assertThatThrownBy(() -> service.save(repairPieceToSave))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("'" + pieceToRemoveStock.getName() + "' has insufficient stock." +
                        " Available: " + pieceToRemoveStock.getStock() + ", Requested: " + repairPieceToSave.getQuantity());
    }
}