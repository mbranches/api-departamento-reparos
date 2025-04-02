package com.branches.model;

import jakarta.persistence.*;

@Entity(name = "reparacao_funcionario")
public class RepairEmployee {
    @EmbeddedId
    private RepairEmployeeKey id;
    @ManyToOne
    @MapsId("idreparacao")
    @JoinColumn(name = "reparacao_idreparacao")
    private Repair repair;
    @ManyToOne
    @MapsId("idfuncionario")
    @JoinColumn(name = "funcionario_idfuncionario")
    private Employee employee;
    @Column(name = "horas_trabalhadas")
    private int hoursWorked;
    @Column(name = "valor_total")
    private double totalValue;
}
