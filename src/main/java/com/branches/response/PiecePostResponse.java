package com.branches.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PiecePostResponse {
    private Long id;
    private String name;
    private double unitValue;
    private int stock;
}
