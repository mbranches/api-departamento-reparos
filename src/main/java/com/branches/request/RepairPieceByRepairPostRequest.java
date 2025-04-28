package com.branches.request;

import jakarta.validation.constraints.PositiveOrZero;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RepairPieceByRepairPostRequest {
    private Long pieceId;
    @PositiveOrZero(message = "'quantity' must be equal to or greater than 0")
    private int quantity;
}
