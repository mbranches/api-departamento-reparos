package com.branches.repository;

import com.branches.model.Piece;
import com.branches.model.Repair;
import com.branches.model.RepairPiece;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RepairPieceRepository extends JpaRepository<RepairPiece, Long> {
    List<RepairPiece> findAllByRepair(Repair repair);

    Optional<RepairPiece> findByRepairAndPiece(Repair repair, Piece piece);
}
