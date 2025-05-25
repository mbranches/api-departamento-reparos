package com.branches.mapper;

import com.branches.model.RepairPiece;
import com.branches.response.RepairPiecePostResponse;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.springframework.context.annotation.Primary;

import java.util.List;

@Primary
@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface RepairPieceMapper {
    RepairPiecePostResponse toRepairPiecePostResponse(RepairPiece repairPiece);

    List<RepairPiecePostResponse> toRepairPiecePostResponseList(List<RepairPiece> repairPieceList);
}
