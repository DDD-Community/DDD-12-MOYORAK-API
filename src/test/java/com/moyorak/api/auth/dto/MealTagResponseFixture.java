package com.moyorak.api.auth.dto;

import java.util.List;

public class MealTagResponseFixture {
    public static MealTagResponse fixture(String dislike, String allergy) {
        return new MealTagResponse(List.of(dislike), List.of(allergy));
    }
}
