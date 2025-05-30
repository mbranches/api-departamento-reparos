package com.branches.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Builder;
import lombok.Data;
import lombok.With;

@Data
@With
@Builder
public class RepairPiecePostRequest {
    @NotNull(message = "The field 'pieceId' cannot be null")
    private Long pieceId;
    @NotNull(message = "The field 'quantity' cannot be null")
    @PositiveOrZero(message = "'quantity' must be equal to or greater than 0")
    private Integer quantity;
}
