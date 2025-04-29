package com.branches.service;

import com.branches.model.Person;
import com.branches.repository.PersonRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PersonService {
    private final PersonRepository repository;

    public Person save(Person personToSave) {
        return repository.save(personToSave);
    }
}
