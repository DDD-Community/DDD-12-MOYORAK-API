package com.moyorak.api.party.service;

import com.moyorak.api.party.domain.PartyAttendee;
import com.moyorak.api.party.repository.PartyAttendeeRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PartyAttendeeService {
    private final PartyAttendeeRepository partyAttendeeRepository;

    public List<PartyAttendee> findByPartyIds(List<Long> partyIds) {
        return partyAttendeeRepository.findByPartyIdIn(partyIds);
    }
}
