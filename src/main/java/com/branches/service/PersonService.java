package com.branches.service;

import com.branches.exception.NotFoundException;
import com.branches.model.Person;
import com.branches.repository.PersonRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PersonService {
    private final PersonRepository repository;

    public Person save(Person personToSave) {
        return repository.save(personToSave);
    }

    public Person update(Person personToUpdate) {
        return repository.save(personToUpdate);
    }

    public Optional<Person> findById(Long id) {
        return repository.findById(id);
    }

    public Person findByIdOrThrowsNotFoundException(Long id) {
        return findById(id)
                .orElseThrow(() -> new NotFoundException("Person not Found"));
    }
}
