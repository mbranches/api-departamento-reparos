package com.branches.service;

import com.branches.exception.BadRequestException;
import com.branches.model.Address;
import com.branches.model.Person;
import com.branches.model.Phone;
import com.branches.repository.PersonRepository;
import com.branches.utils.AddressUtils;
import com.branches.utils.PersonUtils;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.BDDMockito;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class PersonServiceTest {
    @InjectMocks
    private PersonService service;
    @Mock
    private AddressService addressService;
    @Mock
    private PhoneService phoneService;
    @Mock
    private PersonRepository repository;
    private List<Person> personList;

    @BeforeEach
    void init() {
        personList = PersonUtils.newPersonList();
    }

    @Test
    @DisplayName("save returns saved person when successful")
    @Order(1)
    void save_ReturnsSavedPerson_WhenSuccessful() {
        Person personToSave = PersonUtils.newPersonToSave();
        Person expectedResponse = PersonUtils.newPersonSaved();

        Address address = personToSave.getAddress();

        BDDMockito.when(addressService.findAddress(address)).thenReturn(Optional.empty());
        BDDMockito.when(addressService.save(address)).thenReturn(AddressUtils.newAddressSaved());
        BDDMockito.doNothing().when(phoneService).assertPhoneDoesNotExists(personToSave.getPhones().getFirst());
        BDDMockito.when(repository.save(ArgumentMatchers.any())).thenReturn(expectedResponse);

        Person response = service.save(personToSave);

        Assertions.assertThat(response)
                .isNotNull()
                .isEqualTo(expectedResponse);
    }

    @Test
    @DisplayName("save throws BadRequestException when some given phone already exists")
    @Order(2)
    void save_ThrowsBadRequestException_WhenSomeGivenPhoneAlreadyExists() {
        Person personToSave = PersonUtils.newPersonToSave();
        Address address = personToSave.getAddress();

        BDDMockito.when(addressService.findAddress(address)).thenReturn(Optional.empty());
        BDDMockito.when(addressService.save(address)).thenReturn(AddressUtils.newAddressSaved());
        BDDMockito.doThrow(BadRequestException.class).when(phoneService).assertPhoneDoesNotExists(personToSave.getPhones().getFirst());

        Assertions.assertThatThrownBy(() -> service.save(personToSave))
                .isInstanceOf(BadRequestException.class);
    }

    @Test
    @DisplayName("update updates person when successful")
    @Order(3)
    void update_UpdatesPerson_WhenSuccessful() {
        Person personToUpdate = personList.getFirst().withName("Novo Nome");

        Long id = personToUpdate.getId();
        Phone phone = personToUpdate.getPhones().getFirst();
        Address address = personToUpdate.getAddress();

        BDDMockito.when(addressService.findAddress(address)).thenReturn(Optional.empty());
        BDDMockito.doNothing().when(phoneService).assertPhoneDoesNotExists(phone, id);
        BDDMockito.when(phoneService.findPhoneByPerson(phone, id)).thenReturn(Optional.of(phone));
        BDDMockito.when(addressService.save(address)).thenReturn(AddressUtils.newAddressSaved());
        BDDMockito.when(repository.save(ArgumentMatchers.any())).thenReturn(personToUpdate);

        Assertions.assertThatNoException()
                .isThrownBy(() -> service.update(personToUpdate));
    }

    @Test
    @DisplayName("update throws BadRequestException when some given phone already exists")
    @Order(4)
    void update_ThrowsBadRequestException_WheSomeGivenPhoneAlreadyExists() {
        Person personToUpdate = personList.getFirst().withName("Novo Nome");
        Person personPhoneOwner = personList.get(1);
        personToUpdate.setPhones(personPhoneOwner.getPhones());

        Phone phone = personToUpdate.getPhones().getFirst();
        Long id = personToUpdate.getId();

        BDDMockito.doThrow(BadRequestException.class).when(phoneService).assertPhoneDoesNotExists(phone, id);

        Assertions.assertThatThrownBy(() -> service.update(personToUpdate))
                .isInstanceOf(BadRequestException.class);
    }

    @Test
    @DisplayName("findById returns found person when successful")
    @Order(5)
    void findById_ReturnsFoundPerson_WhenSuccessful() {
        Person personToFind = personList.getFirst();
        Long id = personToFind.getId();

        BDDMockito.when(repository.findById(id)).thenReturn(Optional.of(personToFind));

        Optional<Person> response = service.findById(id);

        Assertions.assertThat(response)
                .isNotNull()
                .contains(personToFind);
    }

    @Test
    @DisplayName("findById returns an empty Optional when person not found")
    @Order(6)
    void findById_ReturnsAnEmptyOptional_WhenPersonNotFound() {
        Long randomId = 2131312L;

        BDDMockito.when(repository.findById(randomId)).thenReturn(Optional.empty());

        Optional<Person> response = service.findById(randomId);

        Assertions.assertThat(response)
                .isNotNull()
                .isEmpty();
    }
}