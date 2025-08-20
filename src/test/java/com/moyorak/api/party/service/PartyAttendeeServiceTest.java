package com.moyorak.api.party.service;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;

import com.moyorak.api.party.domain.Party;
import com.moyorak.api.party.domain.PartyAttendee;
import com.moyorak.api.party.domain.PartyAttendeeFixture;
import com.moyorak.api.party.domain.PartyFixture;
import com.moyorak.api.party.dto.PartyAttendRequest;
import com.moyorak.api.party.repository.PartyAttendeeRepository;
import com.moyorak.api.party.repository.PartyRepository;
import com.moyorak.api.team.domain.NotTeamUserException;
import com.moyorak.api.team.domain.Team;
import com.moyorak.api.team.domain.TeamFixture;
import com.moyorak.api.team.domain.TeamUser;
import com.moyorak.api.team.domain.TeamUserFixture;
import com.moyorak.api.team.domain.TeamUserStatus;
import com.moyorak.api.team.repository.TeamUserRepository;
import com.moyorak.config.exception.BusinessException;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class PartyAttendeeServiceTest {

    @InjectMocks private PartyAttendeeService partyAttendeeService;

    @Mock private PartyAttendeeRepository partyAttendeeRepository;

    @Mock private TeamUserRepository teamUserRepository;

    @Mock private PartyRepository partyRepository;

    @Nested
    @DisplayName("파티 참석 시")
    class Attend {

        final Long partyId = 1L;
        final Long userId = 1L;
        final Long teamId = 3L;

        @Test
        @DisplayName("이미 파티에 참여 중이면 BusinessException이 발생한다.")
        void alreadyAttended() {
            // given
            final PartyAttendRequest request = new PartyAttendRequest(partyId, userId);
            final PartyAttendee existingAttendee =
                    PartyAttendeeFixture.fixture(1L, true, partyId, userId);

            given(partyAttendeeRepository.findByPartyIdAndUserIdAndUseTrue(partyId, userId))
                    .willReturn(Optional.of(existingAttendee));

            // when & then
            assertThatThrownBy(() -> partyAttendeeService.attend(request, teamId))
                    .isInstanceOf(BusinessException.class)
                    .hasMessage("이미 파티에 참여하고 있습니다.");
        }

        @Test
        @DisplayName("해당 팀에 가입이 안되어있을 경우 NotTeamUserException이 발생한다.")
        void isNotSameTeam() {
            // given
            final Long otherTeamId = 4L;

            final PartyAttendRequest request = new PartyAttendRequest(partyId, userId);
            final Team requesterTeam = TeamFixture.fixture(teamId, true);
            final TeamUser requesterTeamUser =
                    TeamUserFixture.fixture(userId, requesterTeam, TeamUserStatus.APPROVED, true);
            final Party party = PartyFixture.fixture(partyId, otherTeamId, true);

            given(partyAttendeeRepository.findByPartyIdAndUserIdAndUseTrue(partyId, userId))
                    .willReturn(Optional.empty());
            given(teamUserRepository.findByUserIdAndTeamIdAndUse(userId, teamId, true))
                    .willReturn(Optional.of(requesterTeamUser));
            given(partyRepository.findByIdAndUseTrue(partyId)).willReturn(Optional.of(party));

            // when & then
            assertThatThrownBy(() -> partyAttendeeService.attend(request, teamId))
                    .isInstanceOf(NotTeamUserException.class)
                    .hasMessage("해당 팀의 팀원이 아닙니다.");
        }
    }
}
