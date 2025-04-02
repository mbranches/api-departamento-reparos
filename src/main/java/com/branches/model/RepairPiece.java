package com.branches.model;

import jakarta.persistence.*;

@Entity(name = "reparacao_peca")
public class RepairPiece {
    @EmbeddedId
    private RepairPieceKey id;
    @ManyToOne
    @MapsId("idreparacao")
    @JoinColumn(name = "reparacao_idreparacao")
    private Repair repair;
    @ManyToOne
    @MapsId("idpeca")
    @JoinColumn(name = "peca_idpeca")
    private Piece piece;
    private int quantity;
    private double totalValue;
}
