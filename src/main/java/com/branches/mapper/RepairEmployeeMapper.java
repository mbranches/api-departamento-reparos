package com.branches.mapper;

import com.branches.model.RepairEmployee;
import com.branches.response.RepairEmployeePostResponse;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.springframework.context.annotation.Primary;

import java.util.List;

@Primary
@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface RepairEmployeeMapper {

    RepairEmployeePostResponse toRepairEmployeePostResponse(RepairEmployee repairEmployee);

    List<RepairEmployeePostResponse> toRepairEmployeePostResponseList(List<RepairEmployee> repairEmployeeList);
}
