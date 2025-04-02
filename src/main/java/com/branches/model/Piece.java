package com.branches.model;

import jakarta.persistence.*;
import lombok.Getter;

@Entity(name = "peca")
@Getter
public class Piece {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idpeca")
    private Long id;
    @Column(name = "nome")
    private String name;
    @Column(name = "preco_unitario", columnDefinition = "DECIMAL")
    private double unitValue;
}
