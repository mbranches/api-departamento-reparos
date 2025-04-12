package com.branches.repository;

import com.branches.model.Repair;
import com.branches.model.RepairEmployee;
import com.branches.model.RepairEmployeeKey;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RepairEmployeeRepository extends JpaRepository<RepairEmployee, RepairEmployeeKey> {
    List<RepairEmployee> findAllByRepair(Repair repair);
}
