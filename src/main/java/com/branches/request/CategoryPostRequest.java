package com.branches.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CategoryPostRequest {
    @NotBlank(message = "The field name is required")
    private String name;
    @NotNull(message = "The field hourlyPrice is required")
    @PositiveOrZero(message = "'hourlyPrice' must be equal to or greater than 0")
    private Double hourlyPrice;
}
