package com.branches.repository;

import com.branches.model.Repair;
import com.branches.model.RepairEmployee;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface RepairEmployeeRepository extends JpaRepository<RepairEmployee, Long> {
    List<RepairEmployee> findAllByRepair(Repair repair);

    Optional<RepairEmployee> findByRepair_IdAndEmployee_Id(Long repairId, Long employeeId);
}
