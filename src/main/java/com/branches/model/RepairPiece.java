package com.branches.model;

import jakarta.persistence.*;

@Entity(name = "reparacao_peca")
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
