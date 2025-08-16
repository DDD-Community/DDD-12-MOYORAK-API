package com.moyorak.api.party.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.moyorak.api.party.domain.VoteType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotNull;
import java.time.LocalTime;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Schema(title = "[파티] 파티 생성 요청 DTO - 투표 상세 정보")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PartySaveVoteRequest {

    @NotNull(message = "투표 방식은 필수입니다.")
    @Schema(description = "투표 방식", example = "SELECT")
    private VoteType voteType;

    @Schema(description = "시작 시간", example = "12:00:00")
    private LocalTime fromTime;

    @NotNull(message = "종료 시간은 필수입니다.")
    @Schema(description = "종료 시간", example = "14:00:00")
    private LocalTime toTime;

    @NotNull(message = "식사 시간은 필수입니다.")
    @Schema(description = "식사 시간", example = "15:30:00")
    private LocalTime mealTime;

    @JsonIgnore
    @AssertTrue(message = "투표 방식이 SELECT인 경우 시작 시간은 필수입니다.")
    public boolean isValidStartTime() {
        if (voteType == null) {
            return false;
        }

        if (voteType.isSelect()) {
            return fromTime != null;
        }

        return true;
    }

    @JsonIgnore
    @AssertTrue(message = "시작 시간은 종료 시간보다 빠르거나 같아야 합니다.")
    public boolean isValidTimeRange() {
        if (fromTime == null || toTime == null) {
            return true;
        }
        return !fromTime.isAfter(toTime);
    }

    @JsonIgnore
    @AssertTrue(message = "식사 시간은 종료 시간 이후이며, 시작 시간이 있을 경우 시작 시간 이후여야 합니다.")
    public boolean isMealTimeAfter() {
        if (mealTime == null || toTime == null) {
            return false;
        }

        boolean afterTo = mealTime.isAfter(toTime);

        if (fromTime == null) {
            return afterTo;
        }

        return afterTo && mealTime.isAfter(fromTime);
    }
}
