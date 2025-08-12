package com.moyorak.api.party.service;

import com.moyorak.api.party.dto.PartyAttendeeWithUserProfile;
import com.moyorak.api.party.repository.PartyAttendeeRepository;
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
}
