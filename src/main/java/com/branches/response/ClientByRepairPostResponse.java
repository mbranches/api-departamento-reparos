package com.branches.response;

import com.branches.model.Employee;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ClientByRepairPostResponse {
    private String name;
    private String lastName;
}
