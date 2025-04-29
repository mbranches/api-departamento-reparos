package com.branches.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PersonDefaultResponse {
    private Long id;
    private String name;
    private String lastName;
}
