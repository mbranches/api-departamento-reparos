package com.branches.mapper;

import com.branches.exception.BadRequestException;
import com.branches.model.Piece;
import com.branches.model.RepairPiece;
import com.branches.request.RepairPieceByRepairPostRequest;
import com.branches.response.RepairPieceByRepairResponse;
import com.branches.service.PieceService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class RepairPieceMapper {
    private final PieceService pieceService;

    public List<RepairPiece> toRepairPieceList(List<RepairPieceByRepairPostRequest> postRequest) {
        return postRequest.stream().map(this::toRepairPiece).toList();
    }

    public RepairPiece toRepairPiece(RepairPieceByRepairPostRequest postRequest) {
        Piece piece = pieceService.findByIdOrThrowsNotFoundException(postRequest.getPieceId());
        int quantity = postRequest.getQuantity();

        RepairPiece repairPiece = new RepairPiece();
        repairPiece.setPiece(piece);
        repairPiece.setQuantity(quantity);
        repairPiece.setTotalValue(piece.getUnitValue() * quantity);

        return repairPiece;
    }

    public RepairPieceByRepairResponse toRepairPieceByRepairResponse(RepairPiece repairPiece) {
        return RepairPieceByRepairResponse.builder()
                .piece(repairPiece.getPiece())
                .quantity(repairPiece.getQuantity())
                .totalValue(repairPiece.getTotalValue())
                .build();
    }

    public List<RepairPieceByRepairResponse> toRepairPieceByRepairResponseList(List<RepairPiece> repairPieceList) {
        return repairPieceList.stream().map(this::toRepairPieceByRepairResponse).toList();
    }
}
