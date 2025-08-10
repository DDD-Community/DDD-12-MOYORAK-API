package com.moyorak.api.party.dto;

import com.moyorak.api.party.domain.VoteStatus;
import com.moyorak.api.party.domain.VoteType;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

@Schema(title = "[파티] 파티 목록 조회 응답 DTO")
public record PartyListResponse(
        @Schema(description = "파티 고유 ID", example = "1") Long id,
        @Schema(description = "파티 투표 시작 시간", example = "2025-08-08 12:05:00")
                LocalDateTime startDate,
        @Schema(description = "파티 제목", example = "점심팟 모읍니다.") String title,
        @Schema(description = "파티 투표 타입", example = "SELECT") VoteType voteType,
        @Schema(description = "파티 투표 진행 상태", example = "VOTING") VoteStatus voteStatus,
        @Schema(description = "파티 참가자 수", example = "1") Long attendeeCount,
        @Schema(description = "파티 투표 후보 식당")
                List<PartyRestaurantResponse> partyRestaurantResponseList,
        @Schema(description = "유저 프로필 사진 리스트") List<String> userProfileList,
        @Schema(description = "유저 파티 참가 여부", example = "true") Boolean isParticipating) {
    public static List<PartyListResponse> from(
            List<PartyGeneralInfoProjection> partyGeneralInfoProjection,
            PartyListStore partyListStore) {
        return partyGeneralInfoProjection.stream()
                .map(
                        p ->
                                (new PartyListResponse(
                                        p.id(),
                                        p.startDate(),
                                        p.title(),
                                        p.voteType(),
                                        p.voteStatus(),
                                        p.attendeeCount(),
                                        PartyRestaurantResponse.fromList(
                                                partyListStore.getRestaurantProjection(p.id())),
                                        partyListStore.getUserProfile(p.id()),
                                        partyListStore.getParticipation(p.id()))))
                .toList();
    }

    public static Page<PartyListResponse> toPage(
            List<PartyListResponse> partyListResponseList, Pageable pageable) {
        return new PageImpl<>(partyListResponseList, pageable, partyListResponseList.size());
    }
}
