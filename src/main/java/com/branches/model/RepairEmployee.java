package com.branches.model;

import jakarta.persistence.*;
import lombok.*;

@Entity(name = "reparacao_funcionario")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class RepairEmployee {
    @EmbeddedId
    @EqualsAndHashCode.Include
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
