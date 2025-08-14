package com.moyorak.api.party.service;

import com.moyorak.api.party.domain.Party;
import com.moyorak.api.party.dto.PartyInfo;
import com.moyorak.api.party.repository.PartyRepository;
import com.moyorak.config.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PartyService {

    private final PartyRepository partyRepository;

    @Transactional(readOnly = true)
    public PartyInfo getPartyInfo(final Long partyId) {
        final Party party =
                partyRepository
                        .findByIdAndUseTrue(partyId)
                        .orElseThrow(() -> new BusinessException("파티가 존재하지 않습니다."));
        return PartyInfo.from(party);
    }
}
