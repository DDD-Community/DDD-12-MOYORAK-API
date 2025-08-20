package com.moyorak.api.review.dto;

import java.time.LocalDateTime;

public record ReviewWithUserProjection(
        Long id,
        String extraText,
        Integer score,
        Integer servingTime,
        Integer waitingTime,
        Long userId,
        String name,
        String profileImage,
        LocalDateTime createdDate) {}
