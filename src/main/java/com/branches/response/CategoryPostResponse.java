package com.branches.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CategoryPostResponse {
    private Long id;
    private String name;
    private double hourlyPrice;
}
