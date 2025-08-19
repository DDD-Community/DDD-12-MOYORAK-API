package com.moyorak.api.party.controller;

import com.moyorak.api.auth.domain.UserPrincipal;
import com.moyorak.api.auth.dto.UserDailyStatesResponse;
import com.moyorak.api.party.dto.PartyListRequest;
import com.moyorak.api.party.dto.PartyListResponse;
import com.moyorak.api.party.dto.PartyResponse;
import com.moyorak.api.party.dto.PartySaveRequest;
import com.moyorak.api.party.service.PartyFacade;
import com.moyorak.global.domain.ListResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
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
@Tag(name = "[파티] 파티 API", description = "파티를 위한 API 입니다.")
class PartyController {

    private final PartyFacade partyFacade;

    @GetMapping("/teams/{teamId}/parties/{partyId}")
    @Operation(summary = "파티 상세 조회", description = "파티 상세 조회를 합니다.")
    public PartyResponse getParty(@PathVariable @Positive final Long partyId) {
        return partyFacade.getParty(partyId);
    }

    @GetMapping("/teams/{teamId}/parties")
    @Operation(summary = "파티 목록 조회", description = "파티 목록을 조회 합니다.")
    public ListResponse<PartyListResponse> getParties(
            @PathVariable @Positive final Long teamId,
            @Valid PartyListRequest partyListRequest,
            @AuthenticationPrincipal final UserPrincipal userPrincipal) {
        return partyFacade.getParties(teamId, userPrincipal.getId(), partyListRequest);
    }

    @PostMapping("/teams/{teamId}/parties")
    @Operation(summary = "파티 생성", description = "파티를 생성합니다.")
    public void register(
            @PathVariable @Positive final Long teamId,
            @RequestBody @Valid final PartySaveRequest request) {
        partyFacade.partyRegister(teamId, request);
    }

    @GetMapping("/teams/{teamId}/parties/users")
    @Operation(summary = "파티에 추가할 회원 목록", description = "파티에 추가할 회원에 대한 목록을 조회합니다.")
    public List<UserDailyStatesResponse> getUsers(@PathVariable @Positive final Long teamId) {
        return partyFacade.getUsers(teamId);
    }
}
