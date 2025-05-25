package com.branches.response;

import com.branches.model.Piece;
import lombok.Builder;
import lombok.Data;
import lombok.With;

@Data
@Builder
@With
public class RepairPiecePostResponse {
    private Piece piece;
    private int quantity;
    private double totalValue;
}
