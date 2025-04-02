package com.branches.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Data;

import java.io.Serializable;

@Data
@Embeddable
public class RepairEmployeeKey implements Serializable {
    @Column(name = "reparacao_idreparacao")
    private Long repairId;
    @Column(name = "funcionario_idfuncionario")
    private Long employeeId;
}
