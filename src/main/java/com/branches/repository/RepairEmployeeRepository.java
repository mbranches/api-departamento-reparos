package com.branches.repository;

import com.branches.model.RepairEmployee;
import com.branches.model.RepairEmployeeKey;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RepairEmployeeRepository extends JpaRepository<RepairEmployee, RepairEmployeeKey> {
}
