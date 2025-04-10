package com.branches.response;

import com.branches.model.Piece;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RepairPieceByRepairPostResponse {
    private Piece piece;
    private int quantity;
    private double totalValue;
}
