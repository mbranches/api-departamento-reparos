package com.branches.response;

import com.branches.model.Address;
import com.branches.model.Category;
import com.branches.model.Phone;
import lombok.Data;

import java.util.List;

@Data
public class EmployeePostResponse {
    private Long id;
    private String name;
    private String lastName;
    private Category category;
    private Address address;
    private List<Phone> phones;
}
