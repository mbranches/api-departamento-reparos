package com.branches.service;

import com.branches.exception.BadRequestException;
import com.branches.exception.NotFoundException;
import com.branches.model.*;
import com.branches.repository.RepairPieceRepository;
import com.branches.utils.*;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class RepairPieceServiceTest {
//    @InjectMocks
//    private RepairPieceService service;
//    @Mock
//    private RepairPieceRepository repository;
//    @Mock
//    private PieceService pieceService;
//
//    @Test
//    @DisplayName("findAllByRepair returns all repair pieces from given repair when successful")
//    @Order(1)
//    void findAllByRepair_ReturnsAllRepairPiecesFromGivenRepair_Id_WhenSuccessful() {
//        Repair repairToSearch = RepairUtils.newRepairList().getFirst();
//
//        RepairPiece repairPiece = RepairPieceUtils.newRepairPieceList().getFirst();
//        List<RepairPiece> expectedResponse = List.of(repairPiece);
//
//        BDDMockito.when(repository.findAllByRepair(repairToSearch)).thenReturn(expectedResponse);
//
//        List<RepairPiece> response = service.findAllByRepairId(repairToSearch);
//
//        Assertions.assertThat(response)
//                .isNotNull()
//                .isNotEmpty()
//                .containsExactlyElementsOf(expectedResponse);
//    }
//
//    @Test
//    @DisplayName("findAllByRepair returns an empty list when when given repair contain no pieces")
//    @Order(2)
//    void findAllByRepair_ReturnsEmptyList_WhenGivenRepairIdContainNoPieces() {
//        Repair repairToSearch = RepairUtils.newRepairList().getLast();
//
//        BDDMockito.when(repository.findAllByRepair(repairToSearch)).thenReturn(Collections.emptyList());
//
//        List<RepairPiece> response = service.findAllByRepairId(repairToSearch);
//
//        Assertions.assertThat(response)
//                .isNotNull()
//                .isEmpty();
//    }
//
//    @Test
//    @DisplayName("findByRepairAndPieceOrThrowsNotFoundException returns found RepairPiece when successful")
//    @Order(3)
//    void findByRepairAndPieceOrThrowsNotFoundException_ReturnsFoundRepairIdPiece_Id_WhenSuccessful() {
//        Repair repair = RepairUtils.newRepairList().getFirst();
//        Piece piece = PieceUtils.newPieceList().getFirst();
//
//        RepairPiece expectedResponse = RepairPieceUtils.newRepairPieceList().getFirst();
//
//        BDDMockito.when(repository.findByRepairAndPiece(repair, piece)).thenReturn(Optional.of(expectedResponse));
//
//        RepairPiece response = service.findByRepairAndPieceOrThrowsNotFoundException(repair, piece);
//
//        Assertions.assertThat(response)
//                .isNotNull()
//                .isEqualTo(expectedResponse);
//    }
//
//    @Test
//    @DisplayName("findByRepairAndPieceOrThrowsNotFoundException throws NotFoundException when piece is not found in repair")
//    @Order(4)
//    void findByRepairAndPieceOrThrowsNotFoundException_ThrowsNotFoundException_WhenPieceIsNotFoundInRepairId() {
//        Repair repair = RepairUtils.newRepairList().getFirst();
//        Piece piece = PieceUtils.newPieceList().getLast();
//
//        BDDMockito.when(repository.findByRepairAndPiece(repair, piece)).thenReturn(Optional.empty());
//
//        Assertions.assertThatThrownBy(() -> service.findByRepairAndPieceOrThrowsNotFoundException(repair, piece))
//                .isInstanceOf(NotFoundException.class)
//                .hasMessageContaining("The piece was not found in the repair");
//    }
//
//
//    @Test
//    @DisplayName("saveAll returns saved RepairPieces when successful")
//    @Order(5)
//    void saveAll_ReturnsSavedRepairPieces_WhenSuccessful() {
//        RepairPiece repairPieceToSave = RepairPieceUtils.newRepairPieceToSave();
//        List<RepairPiece> repairPieceList = List.of(repairPieceToSave);
//
//        Piece pieceToRemoveStock = repairPieceToSave.getPiece();
//        int quantityToRemove = repairPieceToSave.getQuantity();
//
//        Piece expectedPiece = repairPieceToSave.getPiece();
//        expectedPiece.setStock(expectedPiece.getStock() - quantityToRemove);
//
//        RepairPiece expectedRepairPiece = RepairPieceUtils.newRepairPieceToSave();
//        expectedRepairPiece.setPiece(expectedPiece);
//
//        BDDMockito.when(pieceService.removesStock(pieceToRemoveStock, quantityToRemove)).thenReturn(expectedPiece);
//        BDDMockito.when(repository.save(expectedRepairPiece)).thenReturn(expectedRepairPiece);
//
//        List<RepairPiece> expectedResponse = List.of(expectedRepairPiece);
//
//        List<RepairPiece> response = service.saveAll(repairPieceList);
//
//        Assertions.assertThat(response)
//                .isNotNull()
//                .isNotEmpty()
//                .containsExactlyElementsOf(expectedResponse);
//    }
//
//    @Test
//    @DisplayName("saveAll throws BadRequestException when quantity is greater than piece stock")
//    @Order(6)
//    void saveAll_ThrowsBadRequestException_WhenQuantityIsGreaterThanPieceStock() {
//        RepairPiece repairPieceToSave = RepairPieceUtils.newRepairPieceToSave();
//        repairPieceToSave.setQuantity(212131);
//        List<RepairPiece> repairPieceList = List.of(repairPieceToSave);
//
//        Piece pieceToRemoveStock = repairPieceToSave.getPiece();
//        int quantityToRemove = repairPieceToSave.getQuantity();
//
//        BDDMockito.when(pieceService.removesStock(pieceToRemoveStock, quantityToRemove))
//                .thenThrow(new BadRequestException("'" + pieceToRemoveStock.getName() + "' has insufficient stock." +
//                " Available: " + pieceToRemoveStock.getStock() + ", Requested: " + repairPieceToSave.getQuantity()));
//
//        Assertions.assertThatThrownBy(() -> service.saveAll(repairPieceList))
//                .isInstanceOf(BadRequestException.class)
//                .hasMessageContaining("'" + pieceToRemoveStock.getName() + "' has insufficient stock." +
//                        " Available: " + pieceToRemoveStock.getStock() + ", Requested: " + repairPieceToSave.getQuantity());
//    }
//
//    @Test
//    @DisplayName("save returns saved RepairPieces when successful")
//    @Order(7)
//    void save_ReturnsSavedRepairPieces_WhenSuccessful() {
//        RepairPiece repairPieceToSave = RepairPieceUtils.newRepairPieceToSave();
//
//        Piece pieceToRemoveStock = repairPieceToSave.getPiece();
//        int quantityToRemove = repairPieceToSave.getQuantity();
//
//        Piece expectedPiece = repairPieceToSave.getPiece();
//        expectedPiece.setStock(expectedPiece.getStock() - quantityToRemove);
//
//        RepairPiece expectedResponse = RepairPieceUtils.newRepairPieceToSave();
//        expectedResponse.setPiece(expectedPiece);
//
//        BDDMockito.when(repository.findAllByRepair(repairPieceToSave.getRepair())).thenReturn(Collections.emptyList());
//        BDDMockito.when(pieceService.removesStock(pieceToRemoveStock, quantityToRemove)).thenReturn(expectedPiece);
//        BDDMockito.when(repository.save(expectedResponse)).thenReturn(expectedResponse);
//
//        RepairPiece response = service.save(repairPieceToSave);
//
//        Assertions.assertThat(response)
//                .isNotNull()
//                .isEqualTo(expectedResponse);
//    }
//
//    @Test
//    @DisplayName("save only removes the stock additional and persists the total quantity summed when the given RepairPiece is registered")
//    @Order(8)
//    void save_OnlyRemovesTheStockAdditionalAndPersistsTheTotalQuantitySummed_WhenTheGivenRepairPieceIsRegistered() {
//        Repair repair = RepairUtils.newRepairList().getFirst();
//
//        RepairPiece savedRepairPiece = RepairPieceUtils.newRepairPieceToSave();
//        savedRepairPiece.setRepair(repair);
//
//        RepairPiece repairPieceToSave = RepairPieceUtils.newRepairPieceToSave();
//        repairPieceToSave.setRepair(repair);
//
//        Piece pieceToRemoveStock = repairPieceToSave.getPiece();
//        int quantityToRemove = repairPieceToSave.getQuantity();
//
//        Piece expectedPiece = repairPieceToSave.getPiece();
//        expectedPiece.setStock(expectedPiece.getStock() - quantityToRemove);
//
//        int totalQuantity = savedRepairPiece.getQuantity() + repairPieceToSave.getQuantity();
//        RepairPiece expectedResponse = RepairPiece.builder()
//                .repair(repair)
//                .piece(expectedPiece)
//                .quantity(totalQuantity)
//                .totalValue(totalQuantity * expectedPiece.getUnitValue())
//                .build();
//
//        BDDMockito.when(repository.findAllByRepair(repairPieceToSave.getRepair())).thenReturn(List.of(savedRepairPiece));
//        BDDMockito.when(pieceService.removesStock(pieceToRemoveStock, quantityToRemove)).thenReturn(expectedPiece);
//        BDDMockito.when(repository.save(expectedResponse)).thenReturn(expectedResponse);
//
//        RepairPiece response = service.save(repairPieceToSave);
//
//        Assertions.assertThat(response)
//                .isNotNull()
//                .isEqualTo(expectedResponse);
//    }
//
//    @Test
//    @DisplayName("save throws BadRequestException when quantity is greater than piece stock")
//    @Order(9)
//    void save_ThrowsBadRequestException_WhenQuantityIsGreaterThanPieceStock() {
//        RepairPiece repairPieceToSave = RepairPieceUtils.newRepairPieceToSave();
//        repairPieceToSave.setQuantity(212131);
//
//        Piece pieceToRemoveStock = repairPieceToSave.getPiece();
//        int quantityToRemove = repairPieceToSave.getQuantity();
//
//        BDDMockito.when(pieceService.removesStock(pieceToRemoveStock, quantityToRemove))
//                .thenThrow(new BadRequestException("'" + pieceToRemoveStock.getName() + "' has insufficient stock." +
//                " Available: " + pieceToRemoveStock.getStock() + ", Requested: " + repairPieceToSave.getQuantity()));
//
//        Assertions.assertThatThrownBy(() -> service.save(repairPieceToSave))
//                .isInstanceOf(BadRequestException.class)
//                .hasMessageContaining("'" + pieceToRemoveStock.getName() + "' has insufficient stock." +
//                        " Available: " + pieceToRemoveStock.getStock() + ", Requested: " + repairPieceToSave.getQuantity());
//    }
//
//    @Test
//    @DisplayName("deleteByRepairAndPiece removes repair piece when successful")
//    @Order(10)
//    void deleteByRepairAndPiece_RemovesRepairPiece_WhenSuccessful() {
//        Repair repair = RepairUtils.newRepairList().getFirst();
//        Piece piece = PieceUtils.newPieceList().getFirst();
//
//        RepairPiece repairPiece = RepairPieceUtils.newRepairPieceList().getFirst();
//
//        BDDMockito.when(repository.findByRepairAndPiece(repair, piece)).thenReturn(Optional.of(repairPiece));
//        BDDMockito.doNothing().when(repository).delete(repairPiece);
//
//        Assertions.assertThatCode(() -> service.deleteByRepairAndPiece(repair, piece))
//                .doesNotThrowAnyException();
//    }
//
//    @Test
//    @DisplayName("deleteByRepairAndPiece throws NotFoundException when piece is not found in the repair")
//    @Order(11)
//    void deleteByRepairAndPiece_ThrowsNotFoundException_WhenPieceIsNotFoundInTheRepair() {
//        Repair repair = RepairUtils.newRepairList().getFirst();
//        Piece piece = PieceUtils.newPieceList().getLast();
//
//        BDDMockito.when(repository.findByRepairAndPiece(repair, piece)).thenReturn(Optional.empty());
//
//        Assertions.assertThatThrownBy(() -> service.deleteByRepairAndPiece(repair, piece))
//                .isInstanceOf(NotFoundException.class)
//                .hasMessageContaining("The piece was not found in the repair");
//    }
}