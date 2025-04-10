package com.branches.response;

import com.branches.model.Category;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class EmployeeByRepairPostResponse {
    private String name;
    private String lastName;
    private Category category;
}
