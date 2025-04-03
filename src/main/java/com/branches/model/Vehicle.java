package com.branches.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity(name = "veiculo")
@Data
public class Vehicle {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idveiculo")
    private Long id;
    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_veiculo")
    private VehicleType vehicleType;
    @Column(name = "marca")
    private String brand;
    @Column(name = "modelo")
    private String model;
    @ManyToOne
    @JoinColumn(name = "fk_cliente_veiculo", referencedColumnName = "idcliente")
    private Client client;
}
