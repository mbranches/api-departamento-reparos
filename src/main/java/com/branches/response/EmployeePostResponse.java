package com.branches.response;

import com.branches.model.Category;
import com.branches.model.Person;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class EmployeePostResponse {
    private Long id;
    private Person person;
    private Category category;
}
