package com.moyorak.api.party.dto;

public class PartyRestaurantAppendRequestFixture {
    public static PartyRestaurantAppendRequest fixture(
            final Long teamRestaurantId, final Long voteId) {
        return new PartyRestaurantAppendRequest(teamRestaurantId, voteId);
    }
}
