package com.branches.repository;

import com.branches.model.RepairPiece;
import com.branches.model.RepairPieceKey;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RepairPieceRepository extends JpaRepository<RepairPiece, RepairPieceKey> {
}
