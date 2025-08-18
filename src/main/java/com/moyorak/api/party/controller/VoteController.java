package com.moyorak.api.party.controller;

import com.moyorak.api.auth.domain.UserPrincipal;
import io.swagger.v3.oas.annotations.Operation;
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
@Tag(name = "[투표] 투표 API", description = "투표를 위한 API 입니다.")
public class VoteController {

    @PostMapping("/teams/{teamId}/parties/{partyId}/votes/{voteId}/records")
    @Operation(summary = "파티 생성", description = "파티를 생성합니다.")
    public void vote(
            @PathVariable @Positive final Long teamId,
            @PathVariable @Positive final Long partyId,
            @PathVariable @Positive final Long voteId,
            @AuthenticationPrincipal final UserPrincipal userPrincipal) {}
}
