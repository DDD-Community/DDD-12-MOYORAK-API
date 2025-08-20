package com.moyorak.api.party.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(title = "[투표자] 투표자 읍닫 DTO")
public record VoterResponse(
        @Schema(description = "선택한 후보 ID", example = "1") Long candidateId,
        @Schema(description = "선택한 후보 ID", example = "1") Long userId,
        @Schema(description = "투표자 이름", example = "홍길동") String name,
        @Schema(description = "투표자 이미지 URL", example = "https://iamge.jpa")
                String profileImageUrl) {
    public static VoterResponse from(final Voter voter) {
        return new VoterResponse(
                voter.candidateId(), voter.userId(), voter.name(), voter.profileImageUrl());
    }
}
