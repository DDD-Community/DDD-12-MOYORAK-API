package com.moyorak.api.party.domain;

import com.moyorak.api.party.dto.PartyInfo;

public class PartyInfoFixture {
    public static PartyInfo fixture(
            final Long id, final String title, final String content, final boolean attendable) {
        return new PartyInfo(id, title, content, attendable);
    }
}
