package com.branches.service;

import com.branches.exception.BadRequestException;
import com.branches.model.Phone;
import com.branches.repository.PhoneRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PhoneService {
    private final PhoneRepository repository;

    public Optional<Phone> findPhoneByPerson(Phone phone, Long id) {
        return repository.findByNumberAndPerson_Id(phone.getNumber(), id);
    }

    public void assertPhoneDoesNotExists(Phone phone) {
        repository.findByNumber(phone.getNumber())
                .ifPresent(p -> {
                    throwsPhoneAlreadyExists(phone);
                });
    }

    public void assertPhoneDoesNotExists(Phone phone, Long id) {
        repository.findByNumberAndPerson_IdNot(phone.getNumber(), id)
                .ifPresent(p -> {
                    throwsPhoneAlreadyExists(phone);
                });
    }

    private void throwsPhoneAlreadyExists(Phone phone) {
        throw new BadRequestException("Phone '%s' belongs to another person".formatted(phone.getNumber()));
    }
}
