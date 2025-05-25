package com.branches.service;

import com.branches.exception.NotFoundException;
import com.branches.mapper.RepairPieceMapper;
import com.branches.model.Piece;
import com.branches.model.Repair;
import com.branches.model.RepairPiece;
import com.branches.repository.RepairPieceRepository;
import com.branches.request.RepairPiecePostRequest;
import com.branches.response.RepairPiecePostResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RepairPieceService {
    private final RepairPieceRepository repository;
    private final RepairPieceMapper mapper;
    private final PieceService pieceService;
    private final RepairService repairService;


    public List<RepairPiecePostResponse> findAllByRepairId(Long repairId) {
        Repair repair = repairService.findByIdOrThrowsNotFoundException(repairId);

        List<RepairPiece> response = repository.findAllByRepair(repair);

        return mapper.toRepairPiecePostResponseList(response);
    }

    private RepairPiece findByRepairAndPieceOrThrowsNotFoundException(Repair repair, Piece piece) {
        return findByRepairIdAndPieceId(repair.getId(), piece.getId())
                .orElseThrow(() -> new NotFoundException("The piece was not found in the repair"));
    }

    private Optional<RepairPiece> findByRepairIdAndPieceId(Long repairId, Long pieceId) {
        return repository.findByRepair_IdAndPiece_Id(repairId, pieceId);
    }

    @Transactional
    public RepairPiecePostResponse save(Long repairId, RepairPiecePostRequest postRequest) {
        Repair repair = repairService.findByIdOrThrowsNotFoundException(repairId);
        Piece pieceNotUpdated = pieceService.findByIdOrThrowsNotFoundException(postRequest.getPieceId());

        Integer quantityToAdd = postRequest.getQuantity();
        Piece piece = pieceService.removesStock(pieceNotUpdated, quantityToAdd);
        RepairPiece repairPieceToSave = RepairPiece.builder().repair(repair).piece(piece).quantity(quantityToAdd).totalValue(piece.getUnitValue() * quantityToAdd).build();
        Optional<RepairPiece> optionalRepairPiece = findByRepairIdAndPieceId(repair.getId(), piece.getId());
        optionalRepairPiece.ifPresent(foundRepairPiece -> {
            int totalQuantity = foundRepairPiece.getQuantity() + quantityToAdd;

            repairPieceToSave.setId(foundRepairPiece.getId());
            repairPieceToSave.setQuantity(totalQuantity);
            repairPieceToSave.setTotalValue(totalQuantity * piece.getUnitValue());
        });


        RepairPiece savedRepairPiece = repository.save(repairPieceToSave);

        repairService.updateTotalValue(repair.getId(), quantityToAdd * piece.getUnitValue());

        return mapper.toRepairPiecePostResponse(savedRepairPiece);
    }

    public void deleteByRepairIdAndPieceId(Long repairId, Long pieceId) {
        Repair repair = repairService.findByIdOrThrowsNotFoundException(repairId);
        Piece piece = pieceService.findByIdOrThrowsNotFoundException(pieceId);


        RepairPiece repairPiece = findByRepairAndPieceOrThrowsNotFoundException(repair, piece);
        double totalValue = repairPiece.getTotalValue();
        repository.deleteById(repairPiece.getId());

        repairService.updateTotalValue(repairId, -totalValue);
    }
}
