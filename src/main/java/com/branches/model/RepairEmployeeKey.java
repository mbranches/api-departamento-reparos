package com.branches.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Embeddable
@AllArgsConstructor
@NoArgsConstructor
public class RepairEmployeeKey implements Serializable {
    @Column(name = "reparacaoid")
    private Long repairId;
    @Column(name = "funcionarioid")
    private Long employeeId;
}
