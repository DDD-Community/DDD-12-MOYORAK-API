package com.moyorak.api.party.service;

import com.moyorak.api.party.domain.PartyAttendee;
import com.moyorak.api.party.dto.PartyAttendRequest;
import com.moyorak.api.party.dto.PartyAttendeeWithUserProfile;
import com.moyorak.api.party.repository.PartyAttendeeRepository;
import com.moyorak.config.exception.BusinessException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PartyAttendeeService {

    private final PartyAttendeeRepository partyAttendeeRepository;

    @Transactional(readOnly = true)
    public List<PartyAttendeeWithUserProfile> findPartyAttendeeWithUserByPartyIds(
            List<Long> partyIds) {
        return partyAttendeeRepository.findPartyAttendeeWithUser(partyIds);
    }

    @Transactional
    public void attend(final PartyAttendRequest request) {
        boolean isPresent =
                partyAttendeeRepository
                        .findByPartyIdAndUserIdAndUseTrue(request.partyId(), request.userId())
                        .isPresent();

        if (isPresent) {
            throw new BusinessException("이미 파티에 참여하고 있습니다.");
        }

        final PartyAttendee partyAttendee = request.toPartyAttendee();
        partyAttendeeRepository.save(partyAttendee);
    }
}
