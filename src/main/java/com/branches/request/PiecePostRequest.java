package com.branches.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PiecePostRequest {
    @NotBlank(message = "The field name is required")
    private String name;
    @NotNull(message = "The field unitValue is required")
    @PositiveOrZero(message = "'unitValue' must be equal to or greater than 0")
    private Double unitValue;
    @PositiveOrZero(message = "'stock' must be equal to or greater than 0")
    private int stock;
}
