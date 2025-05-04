package com.branches.model;

import jakarta.persistence.*;
import lombok.*;

@Entity(name = "reparacao_peca")
@Data
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@NoArgsConstructor
@AllArgsConstructor
public class RepairPiece {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idreparacao_peca")
    @EqualsAndHashCode.Include
    private Long id;
    @ManyToOne
    @JoinColumn(name = "reparacaoid")
    private Repair repair;
    @ManyToOne
    @JoinColumn(name = "pecaid")
    private Piece piece;
    @Column(name = "quantidade")
    private int quantity;
    @Column(name = "valor_total", columnDefinition = "DECIMAL")
    private double totalValue;
}
