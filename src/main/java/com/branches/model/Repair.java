package com.branches.model;

import jakarta.persistence.*;

import java.time.LocalDate;

@Entity(name = "reparacao")
public class Repair {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idreparacao")
    private Long id;
    @ManyToOne
    @JoinColumn(name = "fk_cliente_reparacao", referencedColumnName = "idcliente")
    private Client client;
    @ManyToOne
    @JoinColumn(name = "fk_veiculo_reparacao", referencedColumnName = "idveiculo")
    private Vehicle vehicle;
    @Column(name = "valor_total")
    private double totalValue;
    @Column(name = "data_finalizacao")
    private LocalDate endDate;
}
