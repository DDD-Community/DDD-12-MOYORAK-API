package com.moyorak.api.party.service;

import com.moyorak.api.party.domain.Party;
import com.moyorak.api.party.dto.PartyGeneralInfoProjection;
import com.moyorak.api.party.dto.PartyInfo;
import com.moyorak.api.party.repository.PartyRepository;
import com.moyorak.config.exception.BusinessException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PartyService {

    private final PartyRepository partyRepository;

    @Transactional(readOnly = true)
    public PartyInfo getPartyInfo(final Long partyId) {
        final Party party = getParty(partyId);
        return PartyInfo.from(party);
    }

    @Transactional(readOnly = true)
    public Party getParty(final Long partyId) {
        return partyRepository
                .findByIdAndUseTrue(partyId)
                .orElseThrow(() -> new BusinessException("파티가 존재하지 않습니다."));
    }

    @Transactional(readOnly = true)
    public List<PartyGeneralInfoProjection> findPartyGeneralInfos(Long teamId) {
        return partyRepository.findPartyGeneralInfos(teamId);
    }
}
