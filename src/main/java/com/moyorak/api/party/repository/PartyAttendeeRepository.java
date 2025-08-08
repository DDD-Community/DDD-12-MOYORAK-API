package com.moyorak.api.party.repository;

import com.moyorak.api.party.domain.PartyAttendee;
import java.util.List;
import org.springframework.data.repository.CrudRepository;

public interface PartyAttendeeRepository extends CrudRepository<PartyAttendee, Long> {
    List<PartyAttendee> findByPartyIdIn(List<Long> partyIds);
}
