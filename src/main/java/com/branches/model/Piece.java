package com.branches.model;

import jakarta.persistence.*;

@Entity(name = "peca")
public class Piece {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idpeca")
    private Long id;
    @Column(name = "nome")
    private String name;
    @Column(name = "valor_unitario")
    private double unitValue;
}
