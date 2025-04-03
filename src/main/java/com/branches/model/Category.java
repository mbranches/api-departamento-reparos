package com.branches.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity(name = "categoria")
@Data
public class Category {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idcategoria")
    private Long id;
    @Column(name = "nome")
    private String name;
    @Column(name = "preco_hora",columnDefinition = "DECIMAL")
    private double hourlyPrice;
}
