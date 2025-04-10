package com.branches.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity(name = "reparacao_funcionario")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RepairEmployee {
    @EmbeddedId
    private RepairEmployeeKey id;
    @ManyToOne
    @MapsId("repairId")
    @JoinColumn(name = "reparacaoid")
    private Repair repair;
    @ManyToOne
    @MapsId("repairId")
    @JoinColumn(name = "funcionarioid")
    private Employee employee;
    @Column(name = "horas_trabalhadas")
    private int hoursWorked;
    @Column(name = "valor_total", columnDefinition = "DECIMAL")
    private double totalValue;
}
