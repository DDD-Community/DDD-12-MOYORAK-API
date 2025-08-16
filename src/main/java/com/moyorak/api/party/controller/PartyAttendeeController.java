package com.moyorak.api.party.controller;

import com.moyorak.api.auth.domain.UserPrincipal;
import com.moyorak.api.party.dto.PartyAttendRequest;
import com.moyorak.api.party.service.PartyAttendeeService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Validated
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@SecurityRequirement(name = "JWT")
@Tag(name = "[파티 참석자] 파티 참석자 API", description = "파티 참석자를 위한 API 입니다.")
class PartyAttendeeController {

    private final PartyAttendeeService partyAttendeeService;

    @PostMapping("/teams/{teamId}/parties/{partyId}")
    public void save(
            @PathVariable @Positive final Long partyId,
            @AuthenticationPrincipal final UserPrincipal userPrincipal) {
        partyAttendeeService.attend(PartyAttendRequest.create(partyId, userPrincipal.getId()));
    }
}
