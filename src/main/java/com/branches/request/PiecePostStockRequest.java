package com.branches.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Builder;
import lombok.Data;
import lombok.With;

@Data
@Builder
@With
public class PiecePostStockRequest {
    @NotNull(message = "The field 'quantity' cannot be null")
    @Positive(message = "The field 'quantity' must be positive")
    private Integer quantity;
}
