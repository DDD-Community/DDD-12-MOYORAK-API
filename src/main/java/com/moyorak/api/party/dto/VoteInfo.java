package com.moyorak.api.party.dto;

import com.moyorak.api.party.domain.RandomVoteInfo;
import com.moyorak.api.party.domain.SelectionVoteInfo;
import com.moyorak.api.party.domain.Vote;
import com.moyorak.api.party.domain.VoteStatus;
import com.moyorak.api.party.domain.VoteType;
import java.time.LocalDateTime;

public record VoteInfo(
        Long id,
        VoteType voteType,
        VoteStatus voteStatus,
        Long selectedCandidateId,
        LocalDateTime mealDate,
        LocalDateTime startDate,
        LocalDateTime expiredDate,
        LocalDateTime randomDate) {
    public static VoteInfo from(final Vote vote, final SelectionVoteInfo selectionVoteInfo) {
        return new VoteInfo(
                vote.getId(),
                vote.getType(),
                vote.getStatus(),
                null,
                selectionVoteInfo.getMealDate(),
                selectionVoteInfo.getStartDate(),
                selectionVoteInfo.getExpiredDate(),
                getDefaultDate());
    }

    public static VoteInfo from(final Vote vote, final RandomVoteInfo randomVoteInfo) {
        return new VoteInfo(
                vote.getId(),
                vote.getType(),
                vote.getStatus(),
                randomVoteInfo.getSelectedCandidateId(),
                randomVoteInfo.getMealDate(),
                getDefaultDate(),
                getDefaultDate(),
                randomVoteInfo.getRandomDate());
    }

    private static LocalDateTime getDefaultDate() {
        return LocalDateTime.of(1970, 1, 1, 0, 0);
    }
}
