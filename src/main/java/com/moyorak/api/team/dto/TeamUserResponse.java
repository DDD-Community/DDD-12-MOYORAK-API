package com.moyorak.api.team.dto;

import com.moyorak.api.team.domain.TeamUserStatus;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(title = "[팀 멤버] 팀 멤버 리스트 조회 응답 DTO")
public record TeamUserResponse(
        @Schema(description = "팀 멤버 ID", example = "3") Long teamUserId,
        @Schema(description = "팀 멤버 이름", example = "홍길동") String name,
        @Schema(description = "팀 멤버 이메일", example = "hong@email.com") String email,
        @Schema(description = "팀 멤버 이미지 URL", example = "https://moyorak/hone.jpg")
                String profileImage,
        @Schema(description = "팀 멤버 가입 상태", example = "APPROVED") TeamUserStatus status) {}
