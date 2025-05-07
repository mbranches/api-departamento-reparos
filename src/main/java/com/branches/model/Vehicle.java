package com.branches.model;

import jakarta.persistence.*;
import lombok.*;

@Entity(name = "veiculo")
@Data
@With
@Builder
@NoArgsConstructor
@AllArgsConstructor
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
    @ToString.Exclude
    private Client client;
}
