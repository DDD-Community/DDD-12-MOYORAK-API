package com.moyorak.api.party.service;

import com.moyorak.api.party.domain.Party;
import com.moyorak.api.party.domain.PartyAttendee;
import com.moyorak.api.party.dto.PartyAttendRequest;
import com.moyorak.api.party.dto.PartyAttendeeWithUserProfile;
import com.moyorak.api.party.dto.PartySaveUsersRequest;
import com.moyorak.api.party.repository.PartyAttendeeRepository;
import com.moyorak.api.party.repository.PartyRepository;
import com.moyorak.api.team.domain.NotTeamUserException;
import com.moyorak.api.team.domain.TeamUser;
import com.moyorak.api.team.repository.TeamUserRepository;
import com.moyorak.config.exception.BusinessException;
import java.util.List;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PartyAttendeeService {

    private final PartyAttendeeRepository partyAttendeeRepository;
    private final TeamUserRepository teamUserRepository;
    private final PartyRepository partyRepository;

    @Transactional(readOnly = true)
    public List<PartyAttendeeWithUserProfile> findPartyAttendeeWithUserByPartyIds(
            List<Long> partyIds) {
        return partyAttendeeRepository.findPartyAttendeeWithUser(partyIds);
    }

    @Transactional(readOnly = true)
    public PartyAttendee getPartyAttendeeByUserIdAndPartyId(final Long userId, final Long partyId) {
        return partyAttendeeRepository
                .findByPartyIdAndUserIdAndUseTrue(partyId, userId)
                .orElseThrow(() -> new BusinessException("파티 참가자가 존재하지 않습니다."));
    }

    @Transactional
    public void attend(final PartyAttendRequest request, final Long teamId) {
        final boolean isPresent =
                partyAttendeeRepository
                        .findByPartyIdAndUserIdAndUseTrue(request.partyId(), request.userId())
                        .isPresent();

        if (isPresent) {
            throw new BusinessException("이미 파티에 참여하고 있습니다.");
        }

        final TeamUser teamUser =
                teamUserRepository
                        .findByUserIdAndTeamIdAndUse(request.userId(), teamId, true)
                        .orElseThrow(NotTeamUserException::new);

        final Party party =
                partyRepository
                        .findByIdAndUseTrue(request.partyId())
                        .orElseThrow(() -> new BusinessException("파티가 존재하지 않습니다."));

        if (!party.isAttendable()) {
            throw new BusinessException("참석 불가능한 파티입니다.");
        }

        validateSameTeam(teamUser, party);

        final PartyAttendee partyAttendee = request.toPartyAttendee();
        partyAttendeeRepository.save(partyAttendee);
    }

    private void validateSameTeam(final TeamUser teamUser, final Party party) {
        if (!Objects.equals(teamUser.getTeam().getId(), party.getTeamId())) {
            throw new NotTeamUserException();
        }
    }

    /**
     * 파티 참여 회원을 지정합니다.
     *
     * @param partyId 파티 고유 ID
     * @param users 파티 참여 회원 고유 ID 리스트
     */
    @Transactional
    public void registerUsers(final Long partyId, final PartySaveUsersRequest users) {
        final List<PartyAttendee> partyAttendees =
                users.getIds().stream()
                        .map(it -> PartyAttendee.create(partyId, it.getUserId()))
                        .toList();

        partyAttendeeRepository.saveAll(partyAttendees);
    }

    @Transactional(readOnly = true)
    public boolean existAttendee(final Long partyId, final Long userId) {
        return partyAttendeeRepository.existsByPartyIdAndUserIdAndUseTrue(partyId, userId);
    }
}
