package com.moyorak.api.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(title = "[회원가입] 간편 회원가입 완료 응답 DTO")
public record OAuthResponse(
        @Schema(description = "이메일") String email,
        @Schema(description = "회원 이름") String name,
        @Schema(description = "회원 이미지 URL") String profileImage) {

    public static OAuthResponse create(
            final String email, final String name, final String profileImage) {
        return new OAuthResponse(email, name, profileImage);
    }
}
