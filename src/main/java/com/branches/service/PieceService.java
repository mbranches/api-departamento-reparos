package com.branches.service;

import com.branches.exception.NotFoundException;
import com.branches.mapper.PieceMapper;
import com.branches.model.Piece;
import com.branches.repository.PieceRepository;
import com.branches.request.PiecePostRequest;
import com.branches.response.PieceGetResponse;
import com.branches.response.PiecePostResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class PieceService {
    private final PieceRepository repository;
    private final PieceMapper mapper;

    public List<PieceGetResponse> findAll(String name) {
        List<Piece> response = name == null ? repository.findAll() : repository.findAllByNameContaining(name);

        return mapper.toPieceGetResponseList(response);
    }

    public PieceGetResponse findById(Long id) {
        Piece foundPiece = findByIdOrElseThrowsNotFoundException(id);

        return mapper.toPieceGetResponse(foundPiece);
    }

    public Piece findByIdOrElseThrowsNotFoundException(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new NotFoundException("Piece not Found"));
    }

    public PiecePostResponse save(PiecePostRequest postRequest) {
        Piece pieceToSave = mapper.toPiece(postRequest);

        Piece response = repository.save(pieceToSave);

        return mapper.toPiecePostResponse(response);
    }
}
