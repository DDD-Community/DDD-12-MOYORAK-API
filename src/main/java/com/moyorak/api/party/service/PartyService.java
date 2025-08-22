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
    public PartyInfo getPartyInfo(final Long partyId, final Long teamId) {
        final Party party = getParty(partyId);
        if (!party.isSameTeam(teamId)) {
            throw new BusinessException("팀에 해당 파티가 존재하지 않습니다.");
        }
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

    /**
     * 파티를 생성합니다.
     *
     * @param teamId 팀 고유 ID
     * @param title 파티 투표 제목
     * @param content 파티 설명
     * @return 파티 고유 ID
     */
    @Transactional
    public Long register(
            final Long teamId, final String title, final String content, final boolean attendable) {
        final Party party = Party.create(teamId, title, content, attendable);

        return partyRepository.save(party).getId();
    }
}
