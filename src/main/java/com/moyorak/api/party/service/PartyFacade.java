package com.moyorak.api.party.service;

import com.moyorak.api.party.domain.PartyAttendee;
import com.moyorak.api.party.dto.PartyGeneralInfoProjection;
import com.moyorak.api.party.dto.PartyListRequest;
import com.moyorak.api.party.dto.PartyListResponse;
import com.moyorak.api.party.dto.PartyListStore;
import com.moyorak.api.party.dto.PartyRestaurantProjection;
import com.moyorak.global.domain.ListResponse;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PartyFacade {
    private final PartyService partyService;
    private final PartyAttendeeService partyAttendeeService;
    private final PartyRestaurantService partyRestaurantService;

    @Transactional(readOnly = true)
    public ListResponse<PartyListResponse> getParties(
            final Long teamId, final Long userId, final PartyListRequest partyListRequest) {
        final List<PartyGeneralInfoProjection> parties = partyService.findPartyGeneralInfos(teamId);

        final List<Long> partyIds = parties.stream().map(PartyGeneralInfoProjection::id).toList();

        final List<PartyRestaurantProjection> partyRestaurantProjections =
                partyRestaurantService.findPartyRestaurantInfo(partyIds);

        final List<PartyAttendee> partyAttendees = partyAttendeeService.findByPartyIds(partyIds);

        final PartyListStore partyListStore =
                PartyListStore.create(partyRestaurantProjections, partyAttendees, userId);
        final List<PartyListResponse> partyListResponses =
                PartyListResponse.from(parties, partyListStore);

        return ListResponse.from(
                PartyListResponse.toPage(partyListResponses, partyListRequest.toPageable()));
    }
}
