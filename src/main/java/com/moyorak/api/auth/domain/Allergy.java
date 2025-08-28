package com.moyorak.api.auth.domain;

public record Allergy(Long id, String item) {

    public static Allergy from(final Long id, final String item) {
        return new Allergy(id, item);
    }
}
