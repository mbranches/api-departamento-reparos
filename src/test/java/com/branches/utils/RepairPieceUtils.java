package com.branches.utils;

import com.branches.model.Piece;
import com.branches.model.Repair;
import com.branches.model.RepairPiece;
import com.branches.model.RepairPieceKey;
import com.branches.request.RepairPieceByRepairPostRequest;
import com.branches.response.RepairPieceByRepairResponse;

public class RepairPieceUtils {
    public static RepairPieceByRepairPostRequest newRepairPiecePostRequest() {
        return RepairPieceByRepairPostRequest.builder().pieceId(4L).quantity(5).build();
    }

    public static RepairPiece newRepairPiece() {
        Piece piece = PieceUtils.newPieceToSave();

        return RepairPiece.builder().piece(piece).quantity(5).totalValue(piece.getUnitValue() * 5).build();
    }

    public static RepairPieceByRepairResponse newRepairPieceByRepairPostResponse() {
        Piece piece = PieceUtils.newPieceToSave();

        return RepairPieceByRepairResponse.builder().piece(piece).quantity(5).totalValue(piece.getUnitValue() * 5).build();
    }

    public static RepairPiece newRepairPieceSaved() {
        Repair repair = RepairUtils.newRepairList().getFirst();
        Piece piece = PieceUtils.newPieceList().getFirst();

        RepairPieceKey key = new RepairPieceKey(repair.getId(), piece.getId());
        int quantity = 5;

        return RepairPiece.builder()
                .id(key)
                .repair(repair)
                .piece(piece)
                .quantity(quantity)
                .totalValue(piece.getUnitValue() * quantity)
                .build();
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
