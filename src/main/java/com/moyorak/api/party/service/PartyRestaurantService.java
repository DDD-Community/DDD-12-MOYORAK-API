package com.moyorak.api.party.service;

import com.moyorak.api.party.dto.PartyRestaurantProjection;
import com.moyorak.api.party.repository.PartyRestaurantRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PartyRestaurantService {
    private final PartyRestaurantRepository partyRestaurantRepository;

    @Transactional(readOnly = true)
    public List<PartyRestaurantProjection> findPartyRestaurantInfo(List<Long> partyIds) {
        return partyRestaurantRepository.findPartyRestaurantInfo(partyIds);
    }
}
