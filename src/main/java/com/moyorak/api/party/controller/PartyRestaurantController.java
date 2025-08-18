package com.moyorak.api.party.controller;

import com.moyorak.api.auth.domain.UserPrincipal;
import com.moyorak.api.party.dto.PartyRestaurantAppendRequest;
import com.moyorak.api.party.service.PartyRestaurantService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Validated
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@SecurityRequirement(name = "JWT")
@Tag(name = "[파티 식당] 파티 식당 API", description = "파티 식당을 위한 API 입니다.")
class PartyRestaurantController {

    private final PartyRestaurantService partyRestaurantService;

    @PostMapping("/teams/{teamId}/parties/{partyId}/restaurants")
    @Operation(summary = "파티 식당 추가", description = "파티 식당을 추가합니다.")
    public void append(
            @PathVariable @Positive final Long teamId,
            @PathVariable @Positive final Long partyId,
            @Valid @RequestBody final PartyRestaurantAppendRequest request,
            @AuthenticationPrincipal final UserPrincipal userPrincipal) {
        partyRestaurantService.append(request, teamId, userPrincipal.getId(), partyId);
    }
}
