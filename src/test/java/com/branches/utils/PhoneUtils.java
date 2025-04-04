package com.branches.utils;

import com.branches.model.Phone;
import com.branches.model.PhoneType;

public class PhoneUtils {
    public static Phone newPhone(Long id) {
        return Phone.builder().id(id).number("21121521").phoneType(PhoneType.celular).build();
    }
}
