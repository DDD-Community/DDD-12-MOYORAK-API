package com.moyorak.api.party.dto;

import com.moyorak.api.party.domain.Party;

public record PartyInfo(Long id, String title, String content) {
    public static PartyInfo from(final Party party) {
        return new PartyInfo(party.getId(), party.getTitle(), party.getContent());
    }
}
