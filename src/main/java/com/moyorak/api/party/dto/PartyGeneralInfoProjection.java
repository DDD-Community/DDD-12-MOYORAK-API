package com.moyorak.api.party.dto;

import com.moyorak.api.party.domain.VoteStatus;
import com.moyorak.api.party.domain.VoteType;
import java.time.LocalDateTime;

public record PartyGeneralInfoProjection(
        Long id,
        LocalDateTime startDate,
        LocalDateTime endDate,
        String title,
        VoteType voteType,
        VoteStatus voteStatus,
        Long attendeeCount) {}
