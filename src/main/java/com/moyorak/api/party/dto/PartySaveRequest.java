package com.moyorak.api.party.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonUnwrapped;
import com.moyorak.api.party.domain.VoteType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalTime;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Schema(title = "[파티] 파티 생성 요청 DTO")
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class PartySaveRequest {

    @Size(min = 1, max = 20, message = "투표 제목은 {max}자까지 입력 가능합니다.")
    @Schema(description = "투표 제목", example = "오늘 밥 먹을 사람")
    private String title;

    @Valid @JsonUnwrapped private PartySaveUserSelectionRequest userSelections;

    @Valid @JsonUnwrapped private PartySaveRestaurantSelectionRequest restaurantSelections;

    @Valid @JsonUnwrapped private PartySaveVoteRequest vote;

    @Size(max = 50, message = "파티 설명은 {max}자까지 입력 가능합니다.")
    @Schema(description = "파티 설명", example = "국밥 빼고 다 좋아하는 파티입니다.")
    private String content;

    @NotNull(message = "null값이 들어올 수 없습니다.")
    @Schema(description = "파티 참여 가능 여부", example = "true")
    private Boolean attendable;

    @JsonIgnore
    public boolean isVoteTypeSelect() {
        return getVoteType().isSelect();
    }

    public VoteType getVoteType() {
        return vote.getVoteType();
    }

    @JsonIgnore
    public LocalTime getVoteStartTime() {
        return vote.getFromTime();
    }

    @JsonIgnore
    public LocalTime getVoteEndTime() {
        return vote.getToTime();
    }

    @JsonIgnore
    public LocalTime getPartyMealTime() {
        return vote.getMealTime();
    }
}
