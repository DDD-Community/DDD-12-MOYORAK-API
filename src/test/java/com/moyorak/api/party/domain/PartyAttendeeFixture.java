package com.moyorak.api.party.domain;

import org.springframework.test.util.ReflectionTestUtils;

public class PartyAttendeeFixture {
    public static PartyAttendee fixture(Long id, boolean use, Long partyId, Long userId) {
        PartyAttendee partyAttendee = new PartyAttendee();
        ReflectionTestUtils.setField(partyAttendee, "id", id);
        ReflectionTestUtils.setField(partyAttendee, "use", use);
        ReflectionTestUtils.setField(partyAttendee, "partyId", partyId);
        ReflectionTestUtils.setField(partyAttendee, "userId", userId);
        return partyAttendee;
    }
}
