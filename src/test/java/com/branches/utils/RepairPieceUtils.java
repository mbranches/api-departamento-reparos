package com.branches.utils;

import com.branches.model.Piece;
import com.branches.model.Repair;
import com.branches.model.RepairPiece;
import com.branches.request.RepairPieceByRepairPostRequest;
import com.branches.response.RepairPieceByRepairResponse;

import java.util.List;

public class RepairPieceUtils {
    public static List<RepairPiece> newRepairPieceList() {
        Repair repair = RepairUtils.newRepairList().getFirst();
        int quantity = 5;

        Piece piece1 = PieceUtils.newPieceList().getFirst();
        RepairPiece repairpiece1 = RepairPiece.builder().id(1L).repair(repair).piece(piece1).quantity(quantity).totalValue(piece1.getUnitValue() * quantity).build();

        Piece piece2 = PieceUtils.newPieceList().get(1);
        RepairPiece repairpiece2 = RepairPiece.builder().id(2L).repair(repair).piece(piece2).quantity(quantity).totalValue(piece2.getUnitValue() * quantity).build();

        Piece piece3 = PieceUtils.newPieceList().get(2);
        RepairPiece repairpiece3 = RepairPiece.builder().id(3L).repair(repair).piece(piece3).quantity(quantity).totalValue(piece3.getUnitValue() * quantity).build();

        return List.of(repairpiece1, repairpiece2, repairpiece3);
    }

    public static RepairPieceByRepairPostRequest newRepairPiecePostRequest() {
        return RepairPieceByRepairPostRequest.builder().pieceId(4L).quantity(5).build();
    }

    public static RepairPiece newRepairPieceToSave() {
        Piece piece = PieceUtils.newPieceToSave();

        return RepairPiece.builder().piece(piece).quantity(5).build();
    }

    public static RepairPiece newRepairPieceSaved() {
        RepairPiece repairPiece = newRepairPieceToSave();

        return repairPiece.withId(4L).withTotalValue(repairPiece.getPiece().getUnitValue() * repairPiece.getQuantity());
    }

    public static RepairPieceByRepairResponse newRepairPieceByRepairPostResponse() {
        Piece piece = PieceUtils.newPieceToSave();

        return RepairPieceByRepairResponse.builder().piece(piece).quantity(5).totalValue(piece.getUnitValue() * 5).build();
    }

    public static RepairPieceByRepairResponse newRepairPieceByRepairGetPieces() {
        Piece piece = PieceUtils.newPieceList().getFirst();
        int quantity = 5;


        return RepairPieceByRepairResponse.builder()
                .piece(piece)
                .quantity(quantity)
                .totalValue(piece.getUnitValue() * quantity)
                .build();
    }
}
