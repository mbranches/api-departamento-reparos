package com.branches.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity(name = "peca")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Piece {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idpeca")
    private Long id;
    @Column(name = "nome")
    private String name;
    @Column(name = "preco_unitario", columnDefinition = "DECIMAL")
    private double unitValue;
    @Column(name = "estoque")
    private int stock;
}
