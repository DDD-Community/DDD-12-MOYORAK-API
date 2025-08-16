package com.moyorak.api.party.dto;

import com.moyorak.api.party.domain.VoteStatus;
import com.moyorak.api.party.domain.VoteType;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;

@Schema(title = "[투표] 투표 응답 DTO")
public record VoteResponse(
        @Schema(description = "투표 고유 ID", example = "1") Long id,
        @Schema(description = "투표 타입", example = "SELECT") VoteType voteType,
        @Schema(description = "투표 상태", example = "READY") VoteStatus voteStatus,
        @Schema(description = "랜덤 추첨된 후보 ID(투표 타입이 SELECT이면 null)", example = "1")
                Long randomSelectedCandidateId,
        @Schema(description = "식사 시작 시간", example = "2025-08-14T16:37:13.797598")
                LocalDateTime mealDate,
        @Schema(
                        description = "투표 시작 시간(투표 타입이 RANDOM이면 1970-01-01T00:00:00)",
                        example = "2025-08-14T16:37:13.797598")
                LocalDateTime startDate,
        @Schema(
                        description = "투표 만료 시간(투표 타입이 RANDOM이면 1970-01-01T00:00:00)",
                        example = "2025-08-14T16:37:13.797598")
                LocalDateTime expiredDate,
        @Schema(
                        description = "랜덤 추첨 시간(투표 타입이 SELECT이면 1970-01-01T00:00:00)",
                        example = "2025-08-14T16:37:13.797598")
                LocalDateTime randomDate) {
    public static VoteResponse from(final VoteInfo voteInfo) {
        return new VoteResponse(
                voteInfo.id(),
                voteInfo.voteType(),
                voteInfo.voteStatus(),
                voteInfo.selectedCandidateId(),
                voteInfo.mealDate(),
                voteInfo.startDate(),
                voteInfo.expiredDate(),
                voteInfo.randomDate());
    }
}
