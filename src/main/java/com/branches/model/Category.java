package com.branches.model;

import jakarta.persistence.*;
import lombok.Getter;

@Entity(name = "categoria")
@Getter
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
