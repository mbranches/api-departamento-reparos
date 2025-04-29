package com.branches.response;

import com.branches.model.Person;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class EmployeeGetResponse {
    private Long id;
    private Person person;
    private CategoryGetResponse category;
}
