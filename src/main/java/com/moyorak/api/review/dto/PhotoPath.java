package com.moyorak.api.review.dto;

import io.swagger.v3.oas.annotations.media.Schema;

public record PhotoPath(
        @Schema(description = "사진 경로", example = "https://somepath/review.jpg") String path) {}
