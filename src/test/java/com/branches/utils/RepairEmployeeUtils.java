package com.branches.utils;

import com.branches.model.Category;
import com.branches.model.Employee;
import com.branches.model.Repair;
import com.branches.model.RepairEmployee;
import com.branches.request.RepairEmployeePostRequest;
import com.branches.response.EmployeeByRepairResponse;
import com.branches.response.RepairEmployeePostResponse;

import java.util.List;

public class RepairEmployeeUtils {
    public static List<RepairEmployee> newRepairEmployeeList() {
        List<Employee> employeeList = EmployeeUtils.newEmployeeList();
        Repair repair = RepairUtils.newRepairList().getFirst();
        int hoursWorked = 1;

        Employee employee1 = employeeList.getFirst();
        RepairEmployee repairEmployee1 = RepairEmployee.builder().repair(repair).id(1L).employee(employee1).hoursWorked(hoursWorked).totalValue(employee1.getCategory().getHourlyPrice() * hoursWorked).build();
        Employee employee2 = employeeList.get(1);
        RepairEmployee repairEmployee2 = RepairEmployee.builder().repair(repair).id(2L).employee(employee2).hoursWorked(hoursWorked).totalValue(employee2.getCategory().getHourlyPrice() * hoursWorked).build();
        Employee employee3 = employeeList.getLast();
        RepairEmployee repairEmployee3 = RepairEmployee.builder().repair(repair).id(3L).employee(employee3).hoursWorked(hoursWorked).totalValue(employee3.getCategory().getHourlyPrice() * hoursWorked).build();

        return List.of(repairEmployee1, repairEmployee2, repairEmployee3);
    }


    public static RepairEmployeePostRequest newRepairEmployeePostRequest() {
        return RepairEmployeePostRequest.builder().employeeId(1L).hoursWorked(5).build();
    }

    public static RepairEmployeePostRequest newRepairEmployeePostRequestWithRegisteredEmployee() {
        return RepairEmployeePostRequest.builder().employeeId(1L).hoursWorked(1).build();
    }

    public static RepairEmployeePostResponse newRepairEmployeeByRepairPostResponse() {
        EmployeeByRepairResponse employee = EmployeeUtils.newEmployeeByRepairPostResponse();
        Category employeeCategory = employee.getCategory();

        return RepairEmployeePostResponse.builder().employee(employee).hoursWorked(5).totalValue(employeeCategory.getHourlyPrice() * 5).build();
    }

    public static RepairEmployeePostResponse newRepairEmployeePostResponse() {
        EmployeeByRepairResponse employee = EmployeeUtils.newEmployeeByRepairByAddEmployee();
        Category employeeCategory = employee.getCategory();

        return RepairEmployeePostResponse.builder().employee(employee).hoursWorked(5).totalValue(employeeCategory.getHourlyPrice() * 5).build();
    }

    public static RepairEmployeePostResponse newRepairEmployeeByRepairGetEmployees() {
        Employee employee = EmployeeUtils.newEmployeeList().getFirst();
        int hoursWorked = 1;

        return RepairEmployeePostResponse.builder()
                .employee(EmployeeUtils.newEmployeeByRepairGetEmployees())
                .hoursWorked(hoursWorked)
                .totalValue(employee.getCategory().getHourlyPrice() * hoursWorked)
                .build();
    }

    public static RepairEmployee newRepairEmployeeToSave() {
        Employee employee = EmployeeUtils.newEmployeeSaved();
        Category employeeCategory = employee.getCategory();

        return RepairEmployee.builder().employee(employee).hoursWorked(5).totalValue(employeeCategory.getHourlyPrice() * 5).build();
    }

    public static RepairEmployee newRepairEmployeeSaved() {
        RepairEmployee repairEmployee = newRepairEmployeeToSave();

        Category category = repairEmployee.getEmployee().getCategory();
        return repairEmployee.withId(4L).withTotalValue(category.getHourlyPrice() * repairEmployee.getHoursWorked());

    }
}
