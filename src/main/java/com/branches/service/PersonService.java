package com.branches.service;

import com.branches.model.Address;
import com.branches.model.Person;
import com.branches.model.Phone;
import com.branches.repository.PersonRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PersonService {
    private final PersonRepository repository;
    private final AddressService addressService;
    private final PhoneService phoneService;

    public Person save(Person personToSave) {
        Address address = personToSave.getAddress();

        if (address != null) {
            Optional<Address> addressSearched = addressService.findAddress(address);

            Address addressSaved = addressSearched.orElseGet(() -> addressService.save(address));
            personToSave.setAddress(addressSaved);
        }

        List<Phone> phones = personToSave.getPhones();
        if (phones != null) phones.forEach(phone -> {
            phoneService.assertPhoneDoesNotExists(phone);
            phone.setPerson(personToSave);
        });
        return repository.save(personToSave);
    }

    public Person update(Person personToUpdate) {
        Long id = personToUpdate.getId();

        List<Phone> phones = personToUpdate.getPhones();
        phones.forEach(phone -> {
            phoneService.assertPhoneDoesNotExists(phone, id);

            phone.setPerson(personToUpdate);

            phoneService.findPhoneByPerson(phone, id).ifPresent(foundPhone -> {
                phone.setId(foundPhone.getId());
            });
        });

        Address address = personToUpdate.getAddress();
        Optional<Address> addressSearched = addressService.findAddress(address);

        Address addressSaved = addressSearched.orElseGet(() -> addressService.save(address));
        personToUpdate.setAddress(addressSaved);

        return repository.save(personToUpdate);
    }

    public Optional<Person> findById(Long id) {
        return repository.findById(id);
    }
}
