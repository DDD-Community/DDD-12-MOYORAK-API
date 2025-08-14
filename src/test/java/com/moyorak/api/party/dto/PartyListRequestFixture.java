package com.moyorak.api.party.dto;

public class PartyListRequestFixture {
    public static PartyListRequest fixture(Integer size, Integer currentPage) {
        return new PartyListRequest(size, currentPage);
    }
}
