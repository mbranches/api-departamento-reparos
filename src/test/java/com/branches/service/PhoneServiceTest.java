package com.branches.service;

import com.branches.exception.BadRequestException;
import com.branches.model.Phone;
import com.branches.repository.PhoneRepository;
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
    @DisplayName("assertPhoneDoesNotExists do nothing when the phone not exists")
    @Order(1)
    void assertPhoneDoesNotExists_DoNothing_WhenThePhoneNotExists() {
        Phone phone = PhoneUtils.newPhone(null);

        BDDMockito.when(repository.findByNumber(phone.getNumber())).thenReturn(Optional.empty());

        Assertions.assertThatNoException().isThrownBy(() -> service.assertPhoneDoesNotExists(phone));
    }

    @Test
    @DisplayName("assertPhoneDoesNotExists throws BadRequestException when the phone already exists")
    @Order(2)
    void assertPhoneDoesNotExists_ThrowsBadRequestException_WhenThePhoneAlreadyExists() {
        Phone phone = PhoneUtils.newPhone(null);

        BDDMockito.when(repository.findByNumber(phone.getNumber())).thenReturn(Optional.of(phone));

        Assertions.assertThatThrownBy(() -> service.assertPhoneDoesNotExists(phone))
                .isInstanceOf(BadRequestException.class);
    }
}