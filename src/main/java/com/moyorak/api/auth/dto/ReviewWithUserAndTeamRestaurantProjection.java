package com.moyorak.api.auth.dto;

import java.time.LocalDateTime;

public record ReviewWithUserAndTeamRestaurantProjection(
        Long id,
        String teamRestaurantName,
        Long teamRestaurantId,
        String extraText,
        Integer score,
        Integer servingTime,
        Integer waitingTime,
        Long userId,
        String name,
        String profileImage,
        LocalDateTime createdDate) {}
