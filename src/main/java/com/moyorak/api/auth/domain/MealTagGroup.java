package com.moyorak.api.auth.domain;

import java.util.List;

/** MealTag 리스트를 타입별로 분류하여 보관하는 객체 */
public record MealTagGroup(List<Dislike> dislikes, List<Allergy> allergies) {
    public static MealTagGroup from(List<MealTag> tags) {
        final List<Dislike> dislikes =
                tags.stream()
                        .filter(t -> t.getType().isDislike())
                        .map(t -> Dislike.from(t.getId(), t.getItem()))
                        .toList();

        final List<Allergy> allergies =
                tags.stream()
                        .filter(t -> t.getType().isAllergy())
                        .map(t -> Allergy.from(t.getId(), t.getItem()))
                        .toList();

        return new MealTagGroup(dislikes, allergies);
    }
}
