package com.moyorak.api.party.dto;

import com.moyorak.api.party.domain.PartyAttendee;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class PartyListStore {
    private final Map<Long, List<PartyRestaurantProjection>> restaurantMap;
    private final Map<Long, Boolean> participationMap;

    private PartyListStore(
            final Map<Long, List<PartyRestaurantProjection>> restaurantMap,
            final Map<Long, Boolean> participationMap) {
        this.restaurantMap = restaurantMap;
        this.participationMap = participationMap;
    }

    public static PartyListStore create(
            final List<PartyRestaurantProjection> partyRestaurantProjections,
            final List<PartyAttendee> partyAttendees,
            final Long userId) {
        // 파티별 식당 후보 리스트 맵
        final Map<Long, List<PartyRestaurantProjection>> restaurantMap =
                partyRestaurantProjections.stream()
                        .collect(
                                Collectors.groupingBy(
                                        PartyRestaurantProjection
                                                ::id, // PartyRestaurantProjection에서 파티 ID
                                        Collectors.toList()));

        // 파티별 해당 userId의 참석 여부 맵
        final Map<Long, Boolean> participationMap =
                partyAttendees.stream()
                        .collect(
                                Collectors.groupingBy(
                                        PartyAttendee::getPartyId,
                                        Collectors.collectingAndThen(
                                                Collectors.toList(),
                                                list ->
                                                        list.stream()
                                                                .anyMatch(
                                                                        pa ->
                                                                                pa.getUserId()
                                                                                        .equals(
                                                                                                userId)))));

        return new PartyListStore(restaurantMap, participationMap);
    }

    public List<PartyRestaurantProjection> getRestaurantProjection(final Long partyId) {
        return restaurantMap.getOrDefault(partyId, List.of());
    }

    public Boolean getParticipation(final Long partyId) {
        return participationMap.getOrDefault(partyId, false);
    }
}
