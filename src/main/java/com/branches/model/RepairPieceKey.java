package com.branches.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Embeddable
@Data
@AllArgsConstructor
@NoArgsConstructor
public class RepairPieceKey implements Serializable {
    @Column(name = "reparacaoid")
    private Long repairId;
    @Column(name = "pecaid")
    private Long pieceId;
}
