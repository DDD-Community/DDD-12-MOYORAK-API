package com.moyorak.api.party.controller;

import com.moyorak.api.party.dto.PartyResponse;
import com.moyorak.api.party.service.PartyFacade;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Validated
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@SecurityRequirement(name = "JWT")
@Tag(name = "[파티] 파티 API", description = "파티를 위한 API 입니다.")
class PartyController {

    private final PartyFacade partyFacade;

    @GetMapping("/teams/{teamId}/parties/{partyId}")
    @Operation(summary = "파티 상세 조회", description = "파티 상세 조회를 합니다.")
    public PartyResponse getParty(@PathVariable @Positive final Long partyId) {
        return partyFacade.getParty(partyId);
    }
}
