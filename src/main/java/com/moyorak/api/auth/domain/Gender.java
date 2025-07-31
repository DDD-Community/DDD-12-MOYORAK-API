package com.moyorak.api.auth.domain;

import com.fasterxml.jackson.annotation.JsonCreator;
import org.springframework.util.StringUtils;

public enum Gender {
    MALE,
    FEMALE,
    UNREGISTER;

    @JsonCreator
    public static Gender from(final String input) {
        if (!StringUtils.hasText(input)) {
            return null;
        }

        for (Gender gender : Gender.values()) {
            if (input.equals(gender.name())) {
                return gender;
            }
        }

        return null;
    }
}
