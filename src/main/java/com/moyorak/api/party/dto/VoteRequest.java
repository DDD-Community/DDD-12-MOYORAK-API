package com.moyorak.api.party.dto;

import io.swagger.v3.oas.annotations.media.Schema;

public record VoteRequest(@Schema(description = "투표 후보 고유 ID", example = "1") Long candidateId) {}
