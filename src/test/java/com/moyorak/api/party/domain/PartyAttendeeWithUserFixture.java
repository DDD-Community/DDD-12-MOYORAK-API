package com.moyorak.api.party.domain;

import com.moyorak.api.party.dto.PartyAttendeeWithUserProfile;

public class PartyAttendeeWithUserFixture {
    public static PartyAttendeeWithUserProfile fixture(
            Long partyId, Long userId, String userProfile) {
        return new PartyAttendeeWithUserProfile(partyId, userId, userProfile);
    }
}
