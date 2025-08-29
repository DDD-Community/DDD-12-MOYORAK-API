package com.moyorak.api.auth.domain;

public record Dislike(Long id, String item) {

    public static Dislike from(final Long id, final String item) {
        return new Dislike(id, item);
    }
}
