package com.branches.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity(name = "reparacao_peca")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RepairPiece {
    @EmbeddedId
    private RepairPieceKey id;
    @ManyToOne
    @MapsId("repairId")
    @JoinColumn(name = "reparacaoid")
    private Repair repair;
    @ManyToOne
    @MapsId("pieceId")
    @JoinColumn(name = "pecaid")
    private Piece piece;
    @Column(name = "quantidade")
    private int quantity;
    @Column(name = "valor_total", columnDefinition = "DECIMAL")
    private double totalValue;
}
