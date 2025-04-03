package com.branches.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Data;

import java.io.Serializable;

@Embeddable
@Data
public class RepairPieceKey implements Serializable {
    @Column(name = "reparacaoid")
    private Long repairId;
    @Column(name = "pecaid")
    private Long pieceId;
}
