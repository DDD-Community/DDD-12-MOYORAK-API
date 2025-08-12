package com.moyorak.api.party.service;

import com.moyorak.api.party.dto.PartyGeneralInfoProjection;
import com.moyorak.api.party.repository.PartyRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PartyService {
    private final PartyRepository partyRepository;

    @Transactional(readOnly = true)
    public List<PartyGeneralInfoProjection> findPartyGeneralInfos(Long teamId) {
        return partyRepository.findPartyGeneralInfos(teamId);
    }
}
