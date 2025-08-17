package com.moyorak.api.party.dto;

public record PartyAttendeeWithUserProfile(
        Long partyId, Long userId, String userName, String userProfile) {}
