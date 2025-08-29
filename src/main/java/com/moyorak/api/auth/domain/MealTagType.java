package com.moyorak.api.auth.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum MealTagType {
    DISLIKE("비선호 음식"),
    ALLERGY("알러지");

    private final String description;

    public boolean isDislike() {
        return this == DISLIKE;
    }

    public boolean isAllergy() {
        return this == ALLERGY;
    }
}
