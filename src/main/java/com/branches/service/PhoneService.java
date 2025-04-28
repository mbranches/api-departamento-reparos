package com.branches.service;

import com.branches.exception.BadRequestException;
import com.branches.model.Phone;
import com.branches.repository.PhoneRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PhoneService {
    private final PhoneRepository repository;

    public void assertPhoneDoesNotExists(Phone phone) {
        repository.findByNumberAndPhoneType(phone.getNumber(), phone.getPhoneType())
                .ifPresent(p -> {
                    throw new BadRequestException("Phone '%s' already exists".formatted(phone.getNumber()));
                });
    }
}
