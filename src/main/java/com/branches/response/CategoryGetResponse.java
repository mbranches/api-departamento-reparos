package com.branches.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CategoryGetResponse {
    private Long id;
    private String name;
    private double hourlyPrice;
}