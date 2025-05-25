package com.branches.utils;

import com.branches.model.Piece;
import com.branches.model.Repair;
import com.branches.model.RepairPiece;
import com.branches.request.RepairPiecePostRequest;
import com.branches.response.RepairPiecePostResponse;

import java.util.List;

public class RepairPieceUtils {
    public static List<RepairPiece> newRepairPieceList() {
        Repair repair = RepairUtils.newRepairList().getFirst();
        int quantity = 5;

        Piece piece1 = PieceUtils.newPieceList().getFirst();
        RepairPiece repairPiece1 = RepairPiece.builder().id(1L).repair(repair).piece(piece1).quantity(quantity).totalValue(piece1.getUnitValue() * quantity).build();

        Piece piece2 = PieceUtils.newPieceList().get(1);
        RepairPiece repairPiece2 = RepairPiece.builder().id(2L).repair(repair).piece(piece2).quantity(quantity).totalValue(piece2.getUnitValue() * quantity).build();

        Piece piece3 = PieceUtils.newPieceList().getLast();
        RepairPiece repairPiece3 = RepairPiece.builder().id(3L).repair(repair).piece(piece3).quantity(quantity).totalValue(piece3.getUnitValue() * quantity).build();

        return List.of(repairPiece1, repairPiece2, repairPiece3);
    }

    public static RepairPiecePostRequest newRepairPiecePostRequest() {
        return RepairPiecePostRequest.builder().pieceId(1L).quantity(5).build();
    }

    public static RepairPiece newRepairPieceToSave() {
        Piece piece = PieceUtils.newPieceToSave();

        return RepairPiece.builder().piece(piece).quantity(5).build();
    }

    public static RepairPiece newRepairPieceSaved() {
        RepairPiece repairPiece = newRepairPieceToSave();

        return repairPiece.withId(4L).withTotalValue(repairPiece.getPiece().getUnitValue() * repairPiece.getQuantity());
    }

    public static RepairPiecePostResponse newRepairPieceByRepairPostResponse() {
        Piece piece = PieceUtils.newPieceList().getFirst();

        return RepairPiecePostResponse.builder().piece(piece).quantity(5).totalValue(piece.getUnitValue() * 5).build();
    }

    public static RepairPiecePostResponse newRepairPieceByRepairGetPieces() {
        Piece piece = PieceUtils.newPieceList().getFirst();
        int quantity = 5;


        return RepairPiecePostResponse.builder()
                .piece(piece)
                .quantity(quantity)
                .totalValue(piece.getUnitValue() * quantity)
                .build();
    }
}
