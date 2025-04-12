package com.branches.utils;

import com.branches.model.Category;
import com.branches.model.Employee;
import com.branches.model.RepairEmployee;
import com.branches.model.RepairEmployeeKey;
import com.branches.request.RepairEmployeeByRepairPostRequest;
import com.branches.response.EmployeeByRepairResponse;
import com.branches.response.RepairEmployeeByRepairResponse;

public class RepairEmployeeUtils {
    public static RepairEmployee newRepairEmployeeSaved() {
        RepairEmployeeKey key = new RepairEmployeeKey(1L, 1L);

        Employee employee = EmployeeUtils.newEmployeeList().getFirst();
        int hoursWorked = 1;

        return RepairEmployee.builder()
                .id(key)
                .repair(RepairUtils.newRepairList().getFirst())
                .employee(employee)
                .hoursWorked(hoursWorked)
                .totalValue(employee.getCategory().getHourlyPrice() * hoursWorked)
                .build();
    }

    public static RepairEmployeeByRepairPostRequest newRepairEmployeePostRequest() {
        return RepairEmployeeByRepairPostRequest.builder().employeeId(4L).hoursWorked(5).build();
    }

    public static RepairEmployee newRepairEmployee() {
        Employee employee = EmployeeUtils.newEmployeeToSave();
        Category employeeCategory = employee.getCategory();

        return RepairEmployee.builder().employee(employee).hoursWorked(5).totalValue(employeeCategory.getHourlyPrice() * 5).build();
    }

    public static RepairEmployeeByRepairResponse newRepairEmployeeByRepairPostResponse() {
        EmployeeByRepairResponse employee = EmployeeUtils.newEmployeeByRepairPostResponse();
        Category employeeCategory = employee.getCategory();

        return RepairEmployeeByRepairResponse.builder().employee(employee).hoursWorked(5).totalValue(employeeCategory.getHourlyPrice() * 5).build();
    }
}
