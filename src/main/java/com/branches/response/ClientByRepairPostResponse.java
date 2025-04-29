package com.branches.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ClientByRepairPostResponse {
    private Long id;
    private PersonDefaultResponse person;
    private String email;
}
