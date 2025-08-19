package com.moyorak.api.party.dto;

public class VoteRequestFixture {
    public static VoteRequest fixture(Long candidateId) {
        return new VoteRequest(candidateId);
    }
}
