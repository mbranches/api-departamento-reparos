package com.branches.repository;

import com.branches.model.Repair;
import com.branches.model.RepairPiece;
import com.branches.model.RepairPieceKey;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RepairPieceRepository extends JpaRepository<RepairPiece, RepairPieceKey> {
    List<RepairPiece> findAllByRepair(Repair repair);
}
