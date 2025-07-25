package com.moyorak.api.team.dto;

public record TeamRestaurantViewEvent(Long userId, Long teamId, Long teamRestaurantId) {
    public static TeamRestaurantViewEvent create(Long userId, Long teamId, Long teamRestaurantId) {
        return new TeamRestaurantViewEvent(userId, teamId, teamRestaurantId);
    }
}
