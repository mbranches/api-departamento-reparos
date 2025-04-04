package com.branches.request;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CategoryPostRequest {
    private String name;
    private double hourlyPrice;
}
