package com.moyorak.api.party.dto;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class PartyListStore {
    private final Map<Long, List<PartyRestaurantProjection>> restaurantMap;
    private final Map<Long, Boolean> participationMap;
    private final Map<Long, List<String>> userProfileMap;

    private PartyListStore(
            final Map<Long, List<PartyRestaurantProjection>> restaurantMap,
            final Map<Long, Boolean> participationMap,
            final Map<Long, List<String>> userProfileMap) {
        this.restaurantMap = restaurantMap;
        this.participationMap = participationMap;
        this.userProfileMap = userProfileMap;
    }

    public static PartyListStore create(
            final List<PartyRestaurantProjection> partyRestaurantProjections,
            final List<PartyAttendeeWithUserProfile> partyAttendees,
            final Long userId) {
        // 파티별 식당 후보 리스트 맵
        final Map<Long, List<PartyRestaurantProjection>> restaurantMap =
                partyRestaurantProjections.stream()
                        .collect(
                                Collectors.groupingBy(
                                        PartyRestaurantProjection::id, Collectors.toList()));

        // 파티별 해당 userId의 참석 여부 맵
        final Map<Long, Boolean> participationMap =
                partyAttendees.stream()
                        .collect(
                                Collectors.groupingBy(
                                        PartyAttendeeWithUserProfile::partyId,
                                        Collectors.collectingAndThen(
                                                Collectors.toList(),
                                                list ->
                                                        list.stream()
                                                                .anyMatch(
                                                                        pa ->
                                                                                pa.userId()
                                                                                        .equals(
                                                                                                userId)))));
        // 파티별 유저 프로필 리스트 맵
        final Map<Long, List<String>> userProfileMap =
                partyAttendees.stream()
                        .collect(
                                Collectors.groupingBy(
                                        PartyAttendeeWithUserProfile::partyId,
                                        Collectors.mapping(
                                                PartyAttendeeWithUserProfile::userProfile,
                                                Collectors.toList())));

        return new PartyListStore(restaurantMap, participationMap, userProfileMap);
    }

    public List<PartyRestaurantProjection> getRestaurantProjection(final Long partyId) {
        return restaurantMap.getOrDefault(partyId, List.of());
    }

    public Boolean getParticipation(final Long partyId) {
        return participationMap.getOrDefault(partyId, false);
    }

    public List<String> getUserProfile(final Long partyId) {
        return userProfileMap.getOrDefault(partyId, List.of());
    }
}
