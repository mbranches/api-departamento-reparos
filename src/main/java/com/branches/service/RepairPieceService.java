package com.branches.service;

import com.branches.model.RepairPiece;
import com.branches.repository.RepairPieceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RepairPieceService {
    private final RepairPieceRepository repository;

    public List<RepairPiece> saveAll(List<RepairPiece> repairPiecesToSave) {
        return repository.saveAll(repairPiecesToSave);
    }
}
