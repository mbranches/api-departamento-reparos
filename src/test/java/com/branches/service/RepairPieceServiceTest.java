package com.branches.service;

import com.branches.exception.NotFoundException;
import com.branches.mapper.RepairPieceMapper;
import com.branches.model.*;
import com.branches.repository.RepairPieceRepository;
import com.branches.request.RepairPiecePostRequest;
import com.branches.response.RepairPiecePostResponse;
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
    @InjectMocks
    private RepairPieceService service;
    @Mock
    private RepairPieceRepository repository;
    @Mock
    private PieceService pieceService;
    @Mock
    private RepairService repairService;
    @Mock
    private RepairPieceMapper mapper;
    private List<RepairPiece> repairPieceList;

    @BeforeEach
    void init() {
        repairPieceList = RepairPieceUtils.newRepairPieceList();
    }

    @Test
    @DisplayName("findAllByRepairId returns all repairPieces from given repair id when successful")
    @Order(1)
    void findAllByRepairId_ReturnsAllRepairPiecesFromGivenRepairId_WhenSuccessful() {
        Repair repair = RepairUtils.newRepairList().getFirst();
        Long repairId = repair.getId();

        RepairPiece repairPiece = repairPieceList.getFirst();
        List<RepairPiece> foundRepairPieces = List.of(repairPiece);
        List<RepairPiecePostResponse> expectedResponse = List.of(RepairPieceUtils.newRepairPieceByRepairGetPieces());

        BDDMockito.when(repairService.findByIdOrThrowsNotFoundException(repairId)).thenReturn(repair);
        BDDMockito.when(repository.findAllByRepair(repair)).thenReturn(foundRepairPieces);
        BDDMockito.when(mapper.toRepairPiecePostResponseList(foundRepairPieces)).thenReturn(expectedResponse);

        List<RepairPiecePostResponse> response = service.findAllByRepairId(repairId);

        Assertions.assertThat(response)
                .isNotNull()
                .isNotEmpty()
                .containsExactlyElementsOf(expectedResponse);
    }

    @Test
    @DisplayName("findAllByRepairId returns an empty list when when repair contain no pieces")
    @Order(2)
    void findAllByRepairId_ReturnsEmptyList_WhenRepairContainNoPieces() {
        Repair repair = RepairUtils.newRepairList().getLast();
        Long repairId = repair.getId();

        BDDMockito.when(repairService.findByIdOrThrowsNotFoundException(repairId)).thenReturn(repair);
        BDDMockito.when(repository.findAllByRepair(repair)).thenReturn(Collections.emptyList());
        BDDMockito.when(mapper.toRepairPiecePostResponseList(Collections.emptyList())).thenReturn(Collections.emptyList());

        List<RepairPiecePostResponse> response = service.findAllByRepairId(repairId);

        Assertions.assertThat(response)
                .isNotNull()
                .isEmpty();
    }

    @Test
    @DisplayName("findAllByRepairId throws NotFoundException when given id is not found")
    @Order(3)
    void findAllByRepairId_ThrowsNotFoundException_WhenGivenIdIsNotFound() {
        Long randomId = 121123L;

        BDDMockito.when(repairService.findByIdOrThrowsNotFoundException(randomId)).thenThrow(NotFoundException.class);

        Assertions.assertThatThrownBy(() -> service.findAllByRepairId(randomId))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    @DisplayName("findByRepairAndPieceOrThrowsNotFoundException returns found repairPiece when successful")
    @Order(4)
    void findByRepairAndPieceOrThrowsNotFoundException_ReturnsFoundRepairIdPiece_Id_WhenSuccessful() {
        Repair repair = RepairUtils.newRepairList().getFirst();
        Piece piece = PieceUtils.newPieceList().getFirst();

        RepairPiece expectedResponse = repairPieceList.getFirst();

        BDDMockito.when(repository.findByRepair_IdAndPiece_Id(repair.getId(), piece.getId())).thenReturn(Optional.of(expectedResponse));

        RepairPiece response = service.findByRepairAndPieceOrThrowsNotFoundException(repair, piece);

        Assertions.assertThat(response)
                .isNotNull()
                .isEqualTo(expectedResponse);
    }

    @Test
    @DisplayName("findByRepairAndPieceOrThrowsNotFoundException throws NotFoundException when piece is not found in repair")
    @Order(5)
    void findByRepairAndPieceOrThrowsNotFoundException_ThrowsNotFoundException_WhenPieceIsNotFoundInRepairId() {
        Repair repair = RepairUtils.newRepairList().getFirst();
        Piece piece = PieceUtils.newPieceList().getLast();

        BDDMockito.when(repository.findByRepair_IdAndPiece_Id(repair.getId(), piece.getId())).thenReturn(Optional.empty());

        Assertions.assertThatThrownBy(() -> service.findByRepairAndPieceOrThrowsNotFoundException(repair, piece))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("The piece was not found in the repair");
    }

    @Test
    @DisplayName("save returns saved repairPiece when successful")
    @Order(6)
    void save_ReturnsSavedRepairPiece_WhenSuccessful() {
        Repair repair = RepairUtils.newRepairList().getFirst();
        Long repairId = repair.getId();

        RepairPiecePostRequest postRequest = RepairPieceUtils.newRepairPiecePostRequest();

        RepairPiece savedRepairPiece = RepairPieceUtils.newRepairPieceSaved();

        RepairPiece repairPieceToSave = RepairPieceUtils.newRepairPieceToSave();
        Piece pieceNotUpdated = PieceUtils.newPieceList().getFirst();

        RepairPiecePostResponse postResponse = RepairPieceUtils.newRepairPiecePostResponse();

        BDDMockito.when(repairService.findByIdOrThrowsNotFoundException(repairId)).thenReturn(repair);
        BDDMockito.when(pieceService.findByIdOrThrowsNotFoundException(postRequest.getPieceId())).thenReturn(pieceNotUpdated);
        BDDMockito.when(pieceService.removesStock(pieceNotUpdated, postRequest.getQuantity())).thenReturn(repairPieceToSave.getPiece());
        BDDMockito.when(repository.findByRepair_IdAndPiece_Id(repairId, postRequest.getPieceId())).thenReturn(Optional.empty());
        BDDMockito.when(repository.save(repairPieceToSave)).thenReturn(savedRepairPiece);
        BDDMockito.doNothing().when(repairService).updateTotalValue(repairId, savedRepairPiece.getTotalValue());
        BDDMockito.when(mapper.toRepairPiecePostResponse(savedRepairPiece)).thenReturn(postResponse);

        RepairPiecePostResponse response = service.save(repairId, postRequest);

        Assertions.assertThat(response)
                .isNotNull()
                .isEqualTo(postResponse);
    }

    @Test
    @DisplayName("save updates repairPiece when the given repair already contains the given piece")
    @Order(7)
    void save_UpdatesRepairPiece_WhenTheGivenRepairAlreadyContainsTheGivenPiece() {
        Repair repair = RepairUtils.newRepairList().getFirst();
        Long repairId = repair.getId();

        RepairPiece foundRepairPiece = repairPieceList.getFirst();

        RepairPiecePostRequest postRequest = RepairPieceUtils.newRepairPiecePostRequest();

        RepairPiece repairPieceSaved = RepairPieceUtils.newRepairPieceSaved();
        repairPieceSaved.setId(foundRepairPiece.getId());

        Piece pieceNotUpdated = PieceUtils.newPieceList().getFirst();
        int totalQuantity = foundRepairPiece.getQuantity() + postRequest.getQuantity();
        double totalValue = pieceNotUpdated.getUnitValue() * totalQuantity;
        RepairPiece repairPieceUpdated = RepairPieceUtils.newRepairPieceToSave().withId(foundRepairPiece.getId()).withQuantity(totalQuantity).withTotalValue(totalValue);

        RepairPiecePostResponse postResponse = RepairPieceUtils.newRepairPiecePostResponse().withQuantity(totalQuantity).withTotalValue(totalValue);

        BDDMockito.when(repairService.findByIdOrThrowsNotFoundException(repairId)).thenReturn(repair);
        BDDMockito.when(pieceService.findByIdOrThrowsNotFoundException(postRequest.getPieceId())).thenReturn(pieceNotUpdated);
        BDDMockito.when(pieceService.removesStock(pieceNotUpdated, postRequest.getQuantity())).thenReturn(repairPieceUpdated.getPiece());
        BDDMockito.when(repository.findByRepair_IdAndPiece_Id(repairId, postRequest.getPieceId())).thenReturn(Optional.of(foundRepairPiece));
        BDDMockito.when(repository.save(repairPieceUpdated)).thenReturn(repairPieceSaved);
        BDDMockito.when(mapper.toRepairPiecePostResponse(repairPieceUpdated)).thenReturn(postResponse);

        RepairPiecePostResponse response = service.save(repairId, postRequest);

        Assertions.assertThat(response)
                .isNotNull()
                .isEqualTo(postResponse);
    }

    @Test
    @DisplayName("save throws NotFoundException when the given repairId is not found")
    @Order(8)
    void save_ThrowsNotFoundException_WhenTheGivenRepairIdIsNotFound() {
        Long randomRepairId = 14281267L;

        RepairPiecePostRequest postRequest = RepairPieceUtils.newRepairPiecePostRequest();

        BDDMockito.when(repairService.findByIdOrThrowsNotFoundException(randomRepairId)).thenThrow(NotFoundException.class);

        Assertions.assertThatThrownBy(() -> service.save(randomRepairId, postRequest))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    @DisplayName("save throws BadRequestException when the given piece id is not found")
    @Order(9)
    void save_ThrowsBadRequestException_WhenTheGivenPieceIdIsNotFound() {
        Repair repair = RepairUtils.newRepairList().getFirst();
        Long repairId = repair.getId();

        long randomPieceId = 999L;
        RepairPiecePostRequest postRequest = RepairPieceUtils.newRepairPiecePostRequest().withPieceId(randomPieceId);

        BDDMockito.when(repairService.findByIdOrThrowsNotFoundException(repairId)).thenReturn(repair);
        BDDMockito.when(pieceService.findByIdOrThrowsNotFoundException(randomPieceId)).thenThrow(NotFoundException.class);

        Assertions.assertThatThrownBy(() -> service.save(repairId, postRequest))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    @DisplayName("deleteByRepairIdAndPieceId removes repairPiece when successful")
    @Order(10)
    void deleteByRepairIdAndPieceId_RemovesPieceFromRepair_WhenSuccessful() {
        RepairPiece repairPieceToDelete = repairPieceList.getFirst();

        Repair repair = repairPieceToDelete.getRepair();
        Long repairId = repair.getId();

        Piece piece = repairPieceToDelete.getPiece();
        Long pieceId = piece.getId();

        BDDMockito.when(repairService.findByIdOrThrowsNotFoundException(repairId)).thenReturn(repair);
        BDDMockito.when(pieceService.findByIdOrThrowsNotFoundException(pieceId)).thenReturn(piece);
        BDDMockito.when(repository.findByRepair_IdAndPiece_Id(repairId, pieceId)).thenReturn(Optional.of(repairPieceToDelete));

        Assertions.assertThatCode(() -> service.deleteByRepairIdAndPieceId(repairId, pieceId))
                .doesNotThrowAnyException();
    }

    @Test
    @DisplayName("deleteByRepairIdAndPieceId throws NotFoundException when repair is not found")
    @Order(11)
    void deleteByRepairIdAndPieceId_ThrowsNotFoundException_WhenRepairIsNotFound() {
        Long randomRepairId = 5514121L;
        Piece piece = PieceUtils.newPieceList().getFirst();
        Long pieceId = piece.getId();

        BDDMockito.when(repairService.findByIdOrThrowsNotFoundException(randomRepairId)).thenThrow(NotFoundException.class);

        Assertions.assertThatThrownBy(() -> service.deleteByRepairIdAndPieceId(randomRepairId, pieceId))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    @DisplayName("deleteByRepairIdAndPieceId throws NotFoundException when piece is not found")
    @Order(12)
    void deleteByRepairIdAndPieceId_ThrowsNotFoundException_WhenPieceIsNotFound() {
        Repair repair = RepairUtils.newRepairList().getFirst();
        Long repairId = repair.getId();

        Long randomPieceId = 5514121L;

        BDDMockito.when(repairService.findByIdOrThrowsNotFoundException(repairId)).thenReturn(repair);
        BDDMockito.when(pieceService.findByIdOrThrowsNotFoundException(randomPieceId)).thenThrow(NotFoundException.class);

        Assertions.assertThatThrownBy(() -> service.deleteByRepairIdAndPieceId(repairId, randomPieceId))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    @DisplayName("deleteByRepairIdAndPieceId throws NotFoundException when piece is not found in the repair")
    @Order(13)
    void deleteByRepairIdAndPieceId_ThrowsNotFoundException_WhenPieceIsNotFoundInTheRepair() {
        Repair repair = RepairUtils.newRepairList().getFirst();
        Long repairId = repair.getId();

        Piece piece = PieceUtils.newPieceList().getLast();
        Long pieceId = piece.getId();

        BDDMockito.when(repairService.findByIdOrThrowsNotFoundException(repairId)).thenReturn(repair);
        BDDMockito.when(pieceService.findByIdOrThrowsNotFoundException(pieceId)).thenReturn(piece);
        BDDMockito.when(repository.findByRepair_IdAndPiece_Id(repairId, pieceId)).thenReturn(Optional.empty());

        Assertions.assertThatCode(() -> service.deleteByRepairIdAndPieceId(repairId, pieceId))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("The piece was not found in the repair");
    }
}