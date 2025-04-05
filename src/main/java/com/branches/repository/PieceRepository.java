package com.branches.repository;

import com.branches.model.Piece;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PieceRepository extends JpaRepository<Piece, Long> {
    List<Piece> findAllByNameContaining(String name);
}
