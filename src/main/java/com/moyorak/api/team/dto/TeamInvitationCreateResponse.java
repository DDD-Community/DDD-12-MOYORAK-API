package com.moyorak.api.team.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(title = "[팀 초대] 팀 초대 링크 토큰 생성 응답 DTO")
public record TeamInvitationCreateResponse(
        @Schema(description = "팀 초대 링크 토큰", example = "abcdefg-asddf") String invitationToken) {
    public static TeamInvitationCreateResponse of(String invitationToken) {
        return new TeamInvitationCreateResponse(invitationToken);
    }
}
