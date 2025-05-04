package com.branches.service;

import com.branches.exception.BadRequestException;
import com.branches.model.Person;
import com.branches.model.Phone;
import com.branches.repository.PhoneRepository;
import com.branches.utils.PersonUtils;
import com.branches.utils.PhoneUtils;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

@ExtendWith(MockitoExtension.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class PhoneServiceTest {
    @InjectMocks
    private PhoneService service;
    @Mock
    private PhoneRepository repository;

    @Test
    @DisplayName("findPhoneByPerson returns found phone when successful")
    @Order(1)
    void findPhoneByPerson_ReturnsFoundPhone_WhenSuccessful() {
        Person person = PersonUtils.newPersonList().getFirst();
        Phone phone = person.getPhones().getFirst();
        Long id = person.getId();

        BDDMockito.when(repository.findByNumberAndPerson_Id(phone.getNumber(), id)).thenReturn(Optional.of(phone));

        Optional<Phone> response = service.findPhoneByPerson(phone, id);

        Assertions.assertThat(response)
                .isNotNull()
                .contains(phone);
    }

    @Test
    @DisplayName("findPhoneByPerson returns an empty optional when the phone not found")
    @Order(2)
    void findPhoneByPerson_ReturnsAnEmptyOptional_WhenThePhoneNotFound() {
        Person person = PersonUtils.newPersonList().getFirst();
        Phone phone = person.getPhones().getFirst();
        Long id = person.getId();

        BDDMockito.when(repository.findByNumberAndPerson_Id(phone.getNumber(), id)).thenReturn(Optional.empty());

        Optional<Phone> response = service.findPhoneByPerson(phone, id);

        Assertions.assertThat(response)
                .isNotNull()
                .isEmpty();
    }

    @Test
    @DisplayName("assertPhoneDoesNotExists do nothing when the phone not exists")
    @Order(3)
    void assertPhoneDoesNotExists_DoNothing_WhenThePhoneNotExists() {
        Phone phone = PhoneUtils.newPhone(null);

        BDDMockito.when(repository.findByNumber(phone.getNumber())).thenReturn(Optional.empty());

        Assertions.assertThatNoException().isThrownBy(() -> service.assertPhoneDoesNotExists(phone));
    }

    @Test
    @DisplayName("assertPhoneDoesNotExists do nothing when the phone belongs to the given person")
    @Order(4)
    void assertPhoneDoesNotExists_DoNothing_WhenThePhoneBelongToTheGivenPerson() {
        Person personToFind = PersonUtils.newPersonList().getFirst();
        Phone phoneToFind = personToFind.getPhones().getFirst();

        Long id = phoneToFind.getId();

        BDDMockito.when(repository.findByNumberAndPerson_IdNot(phoneToFind.getNumber(), id)).thenReturn(Optional.empty());

        Assertions.assertThatNoException().isThrownBy(() -> service.assertPhoneDoesNotExists(phoneToFind, id));
    }

    @Test
    @DisplayName("assertPhoneDoesNotExists throws BadRequestException when the phone does not belong to the given person")
    @Order(5)
    void assertPhoneDoesNotExists_ThrowsBadRequestException_WhenThePhoneDoesNotBelongToTheGivenPerson() {
        Person personPhoneOwner = PersonUtils.newPersonList().get(1);

        Person personToFind = PersonUtils.newPersonList().getFirst().withPhones(personPhoneOwner.getPhones());
        Phone phoneToFind = personToFind.getPhones().getFirst();

        Long id = phoneToFind.getId();

        BDDMockito.when(repository.findByNumberAndPerson_IdNot(phoneToFind.getNumber(), id)).thenReturn(Optional.of(personPhoneOwner));

        Assertions.assertThatThrownBy(() -> service.assertPhoneDoesNotExists(phoneToFind, id))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("Phone '%s' already exists for another person", phoneToFind.getNumber());
    }

    @Test
    @DisplayName("assertPhoneDoesNotExists throws BadRequestException when the phone already exists")
    @Order(6)
    void assertPhoneDoesNotExists_ThrowsBadRequestException_WhenThePhoneAlreadyExists() {
        Phone phone = PhoneUtils.newPhone(null);

        BDDMockito.when(repository.findByNumber(phone.getNumber())).thenReturn(Optional.of(phone));

        Assertions.assertThatThrownBy(() -> service.assertPhoneDoesNotExists(phone))
                .isInstanceOf(BadRequestException.class);
    }
}