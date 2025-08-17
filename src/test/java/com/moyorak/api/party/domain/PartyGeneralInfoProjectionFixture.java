package com.moyorak.api.party.domain;

import com.moyorak.api.party.dto.PartyGeneralInfoProjection;
import java.time.LocalDateTime;

public class PartyGeneralInfoProjectionFixture {
    public static PartyGeneralInfoProjection fixture(
            Long id,
            LocalDateTime startDate,
            LocalDateTime endDate,
            String title,
            VoteType voteType,
            VoteStatus voteStatus,
            Long attendeeCount) {
        return new PartyGeneralInfoProjection(
                id, startDate, endDate, title, voteType, voteStatus, attendeeCount);
    }
}
