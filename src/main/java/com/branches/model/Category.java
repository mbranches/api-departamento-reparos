package com.branches.model;

import jakarta.persistence.*;

@Entity(name = "categoria")
public class Category {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idcategoria")
    private Long id;
    @Column(name = "nome")
    private String name;
    @Column(name = "preco_hora")
    private double hourlyPrice;
}
