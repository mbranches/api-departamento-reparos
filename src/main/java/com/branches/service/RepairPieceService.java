package com.branches.service;

import com.branches.exception.NotFoundException;
import com.branches.model.Piece;
import com.branches.model.Repair;
import com.branches.model.RepairPiece;
import com.branches.repository.RepairPieceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RepairPieceService {
    private final RepairPieceRepository repository;
    private final PieceService pieceService;

    public List<RepairPiece> findAllByRepair(Repair repair) {
        return repository.findAllByRepair(repair);
    }

    public RepairPiece findByRepairAndPieceOrThrowsNotFoundException(Repair repair, Piece piece) {
        return repository.findByRepairAndPiece(repair, piece)
                .orElseThrow(() -> new NotFoundException("The piece was not found in the repair"));
    }

    public List<RepairPiece> saveAll(List<RepairPiece> repairPiecesToSave) {
        return repairPiecesToSave.stream().map(this::save).toList();
    }

    public RepairPiece save(RepairPiece repairPiece) {
        Piece piece = pieceService.removesStock(repairPiece.getPiece(), repairPiece.getQuantity());
        repairPiece.setPiece(piece);

        return repository.save(repairPiece);
    }

    public void deleteByRepairAndPiece(Repair repair, Piece piece) {
        repository.delete(findByRepairAndPieceOrThrowsNotFoundException(repair, piece));
    }
}
