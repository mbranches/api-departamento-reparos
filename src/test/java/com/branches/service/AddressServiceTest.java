package com.branches.service;

import com.branches.model.Address;
import com.branches.repository.AddressRepository;
import com.branches.utils.AddressUtils;
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
class AddressServiceTest {
    @InjectMocks
    private AddressService service;
    @Mock
    private AddressRepository repository;
    private List<Address> addressList;

    @BeforeEach
    void init() {
        addressList = AddressUtils.newAddressList();
    }

    @Test
    @DisplayName("findAddress returns the found address when successful")
    @Order(1)
    void findAddress_ReturnsFoundAddress_WhenSuccessful() {
        Address addressToSearch = addressList.getFirst();

        BDDMockito.when(repository.findByStreetAndDistrictAndCityAndState(
                ArgumentMatchers.anyString(),
                ArgumentMatchers.anyString(),
                ArgumentMatchers.anyString(),
                ArgumentMatchers.anyString()
                )
        ).thenReturn(Optional.of(addressToSearch));

        addressToSearch.setId(null);

        Optional<Address> response = service.findAddress(addressToSearch);

        Assertions.assertThat(response)
                .isNotEmpty()
                .isPresent()
                .containsSame(addressToSearch);
    }

    @Test
    @DisplayName("findAddress returns an empty optional when address is not found")
    @Order(2)
    void findAddress_ReturnsEmptyOptional_WhenAddressIsNotFound() {
        Address addressNotSaved = AddressUtils.newAddressToSave();

        BDDMockito.when(repository.findByStreetAndDistrictAndCityAndState(
                ArgumentMatchers.anyString(),
                ArgumentMatchers.anyString(),
                ArgumentMatchers.anyString(),
                ArgumentMatchers.anyString()
                )
        ).thenReturn(Optional.empty());

        Optional<Address> response = service.findAddress(addressNotSaved);

        Assertions.assertThat(response)
                .isNotNull()
                .isEmpty();
    }

    @Test
    @DisplayName("save returns saved address when successful")
    @Order(3)
    void save_ReturnsSavedAddress_WhenSuccessful() {
        Address addressToSave = AddressUtils.newAddressToSave();

        BDDMockito.when(repository.save(addressToSave)).thenReturn(addressToSave);

        Address response = service.save(addressToSave);

        Assertions.assertThat(response)
                .isNotNull()
                .isEqualTo(addressToSave);
    }
}