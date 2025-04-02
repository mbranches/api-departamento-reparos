package com.branches.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

import java.io.Serializable;

@Embeddable
public class RepairPieceKey implements Serializable {
    @Column(name = "reparacao_idreparacao")
    private Long repairId;
    @Column(name = "peca_idpeca")
    private Long pieceId;
}
