package com.moyorak.api.party.domain;

import org.springframework.test.util.ReflectionTestUtils;

public class PartyFixture {
    public static Party fixture(final Long id, final Long teamId) {
        Party party = new Party();

        ReflectionTestUtils.setField(party, "id", id);
        ReflectionTestUtils.setField(party, "teamId", teamId);

        return party;
    }
}
