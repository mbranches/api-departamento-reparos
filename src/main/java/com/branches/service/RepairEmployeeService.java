package com.branches.service;

import com.branches.model.RepairEmployee;
import com.branches.repository.RepairEmployeeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RepairEmployeeService {
    private final RepairEmployeeRepository repository;

    public List<RepairEmployee> saveAll(List<RepairEmployee> repairPiecesToSave) {
        return repository.saveAll(repairPiecesToSave);
    }
}
