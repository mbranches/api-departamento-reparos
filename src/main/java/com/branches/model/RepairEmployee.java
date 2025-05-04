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
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idreparacao_funcionario")
    @EqualsAndHashCode.Include
    private Long id;
    @ManyToOne
    @JoinColumn(name = "reparacaoid")
    private Repair repair;
    @ManyToOne
    @JoinColumn(name = "funcionarioid")
    private Employee employee;
    @Column(name = "horas_trabalhadas")
    private int hoursWorked;
    @Column(name = "valor_total", columnDefinition = "DECIMAL")
    private double totalValue;
}
