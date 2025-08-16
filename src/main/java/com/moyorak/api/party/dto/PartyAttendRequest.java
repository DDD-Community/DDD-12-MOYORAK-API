package com.moyorak.api.party.dto;

import com.moyorak.api.party.domain.PartyAttendee;

public record PartyAttendRequest(Long partyId, Long userId) {
    public static PartyAttendRequest create(final Long partyId, final Long userId) {
        return new PartyAttendRequest(partyId, userId);
    }

    public PartyAttendee toPartyAttendee() {
        return PartyAttendee.create(partyId, userId);
    }
}
