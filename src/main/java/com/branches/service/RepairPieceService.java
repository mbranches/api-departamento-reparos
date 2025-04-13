package com.branches.service;

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

    public List<RepairPiece> saveAll(List<RepairPiece> repairPiecesToSave) {
        return repairPiecesToSave.stream().map(this::save).toList();
    }

    public RepairPiece save(RepairPiece repairPiece) {
        Piece piece = pieceService.removesStock(repairPiece.getPiece(), repairPiece.getQuantity());
        repairPiece.setPiece(piece);

        return repository.save(repairPiece);
    }

    public List<RepairPiece> findAllByRepair(Repair repair) {
        return repository.findAllByRepair(repair);
    }
}
