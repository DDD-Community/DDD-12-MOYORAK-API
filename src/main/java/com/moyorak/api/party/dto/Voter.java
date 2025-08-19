package com.moyorak.api.party.dto;

public record Voter(Long candidateId, Long userId, String name, String profileImageUrl) {}
