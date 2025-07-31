package com.moyorak.api.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(title = "[회원가입] 회원가입 완료 응답 DTO")
public record SignUpResponse(@Schema(description = "회원 고유 ID") Long userId) {

    public static SignUpResponse create(final Long userId) {
        return new SignUpResponse(userId);
    }
}
