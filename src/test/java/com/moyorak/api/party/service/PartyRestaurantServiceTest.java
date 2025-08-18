package com.moyorak.api.party.service;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;

import com.moyorak.api.party.domain.Party;
import com.moyorak.api.party.domain.PartyFixture;
import com.moyorak.api.party.domain.Vote;
import com.moyorak.api.party.domain.VoteFixture;
import com.moyorak.api.party.domain.VoteRestaurantCandidate;
import com.moyorak.api.party.domain.VoteRestaurantCandidateFixture;
import com.moyorak.api.party.domain.VoteStatus;
import com.moyorak.api.party.domain.VoteType;
import com.moyorak.api.party.dto.PartyRestaurantAppendRequest;
import com.moyorak.api.party.dto.PartyRestaurantAppendRequestFixture;
import com.moyorak.api.party.repository.PartyRepository;
import com.moyorak.api.party.repository.PartyRestaurantRepository;
import com.moyorak.api.party.repository.VoteRepository;
import com.moyorak.api.team.domain.NotTeamUserException;
import com.moyorak.api.team.domain.Team;
import com.moyorak.api.team.domain.TeamFixture;
import com.moyorak.api.team.domain.TeamRole;
import com.moyorak.api.team.domain.TeamUser;
import com.moyorak.api.team.domain.TeamUserFixture;
import com.moyorak.api.team.domain.TeamUserStatus;
import com.moyorak.api.team.repository.TeamUserRepository;
import com.moyorak.config.exception.BusinessException;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class PartyRestaurantServiceTest {

    @InjectMocks private PartyRestaurantService partyRestaurantService;

    @Mock private PartyRestaurantRepository partyRestaurantRepository;
    @Mock private TeamUserRepository teamUserRepository;
    @Mock private PartyRepository partyRepository;
    @Mock private VoteRepository voteRepository;

    @Nested
    @DisplayName("append 메서드는")
    class Append {

        private final Long teamId = 1L;
        private final Long userId = 100L;
        private final Long partyId = 10L;
        private final Long voteId = 10L;
        private final Long teamRestaurantId = 123L;

        final PartyRestaurantAppendRequest request =
                PartyRestaurantAppendRequestFixture.fixture(teamRestaurantId, voteId);

        @Test
        @DisplayName("유저가 팀에 속하지 않으면 NotTeamUserException을 발생시킨다.")
        void notTeamUser() {
            // given
            given(teamUserRepository.findByUserIdAndTeamIdAndUse(userId, teamId, true))
                    .willReturn(Optional.empty());

            // when & then
            assertThatThrownBy(
                            () -> partyRestaurantService.append(request, teamId, userId, partyId))
                    .isInstanceOf(NotTeamUserException.class)
                    .hasMessage("해당 팀의 팀원이 아닙니다.");
        }

        @Test
        @DisplayName("파티가 존재하지 않으면 BusinessException을 발생시킨다.")
        void notFoundParty() {
            // given
            final TeamUser teamUser =
                    TeamUserFixture.fixture(TeamUserStatus.APPROVED, TeamRole.TEAM_MEMBER, true);

            given(teamUserRepository.findByUserIdAndTeamIdAndUse(userId, teamId, true))
                    .willReturn(Optional.of(teamUser));
            given(partyRepository.findByIdAndUseTrue(partyId)).willReturn(Optional.empty());

            // when & then
            assertThatThrownBy(
                            () -> partyRestaurantService.append(request, teamId, userId, partyId))
                    .isInstanceOf(BusinessException.class)
                    .hasMessage("파티가 존재하지 않습니다.");
        }

        @Test
        @DisplayName("투표가 존재하지 않으면 예외를 발생시킨다.")
        void notFoundVote() {
            // given
            final Party party = PartyFixture.fixture(partyId, teamId);

            final Long TeamUserId = 13L;
            final Team team = TeamFixture.fixture(teamId, true);
            final TeamUser teamUser =
                    TeamUserFixture.fixture(
                            TeamUserId,
                            team,
                            userId,
                            TeamUserStatus.APPROVED,
                            TeamRole.TEAM_MEMBER,
                            true);

            given(teamUserRepository.findByUserIdAndTeamIdAndUse(userId, teamId, true))
                    .willReturn(Optional.of(teamUser));
            given(partyRepository.findByIdAndUseTrue(partyId)).willReturn(Optional.of(party));

            given(voteRepository.findByIdAndPartyIdAndUseTrue(request.voteId(), party.getId()))
                    .willReturn(Optional.empty());

            // when & then
            assertThatThrownBy(
                            () -> partyRestaurantService.append(request, teamId, userId, partyId))
                    .isInstanceOf(BusinessException.class)
                    .hasMessage("투표가 존재하지 않습니다.");
        }

        @Test
        @DisplayName("팀에 파티가 존재하지 않을 경우 예외를 발생시킨다.")
        void partyNotInTeam() {
            // given
            final Long otherTeamId = 13L;
            final Party party = PartyFixture.fixture(partyId, otherTeamId);

            final Long TeamUserId = 13L;
            final Team team = TeamFixture.fixture(teamId, true);
            final TeamUser teamUser =
                    TeamUserFixture.fixture(
                            TeamUserId,
                            team,
                            userId,
                            TeamUserStatus.APPROVED,
                            TeamRole.TEAM_MEMBER,
                            true);

            given(teamUserRepository.findByUserIdAndTeamIdAndUse(userId, teamId, true))
                    .willReturn(Optional.of(teamUser));
            given(partyRepository.findByIdAndUseTrue(partyId)).willReturn(Optional.of(party));

            // when & then
            assertThatThrownBy(
                            () -> partyRestaurantService.append(request, teamId, userId, partyId))
                    .isInstanceOf(BusinessException.class)
                    .hasMessage("해당 팀에 존재하지 않는 파티입니다.");
        }

        @Test
        @DisplayName("후보 식당이 5개 있으면 예외를 발생시킨다.")
        void candidatesFull() {
            // given
            final Party party = PartyFixture.fixture(partyId, teamId);

            final Long TeamUserId = 13L;
            final Team team = TeamFixture.fixture(teamId, true);
            final TeamUser teamUser =
                    TeamUserFixture.fixture(
                            TeamUserId,
                            team,
                            userId,
                            TeamUserStatus.APPROVED,
                            TeamRole.TEAM_MEMBER,
                            true);

            given(teamUserRepository.findByUserIdAndTeamIdAndUse(userId, teamId, true))
                    .willReturn(Optional.of(teamUser));
            given(partyRepository.findByIdAndUseTrue(partyId)).willReturn(Optional.of(party));

            Vote vote = VoteFixture.fixture(VoteType.RANDOM, VoteStatus.VOTING, true, partyId);
            given(voteRepository.findByIdAndPartyIdAndUseTrue(request.voteId(), partyId))
                    .willReturn(Optional.of(vote));

            final List<VoteRestaurantCandidate> candidates =
                    List.of(
                            VoteRestaurantCandidateFixture.fixture(1L, voteId, 111L, true),
                            VoteRestaurantCandidateFixture.fixture(2L, voteId, 112L, true),
                            VoteRestaurantCandidateFixture.fixture(3L, voteId, 113L, true),
                            VoteRestaurantCandidateFixture.fixture(4L, voteId, 114L, true),
                            VoteRestaurantCandidateFixture.fixture(5L, voteId, 115L, true));
            given(partyRestaurantRepository.findAllByVoteIdAndUseTrueForUpdate(request.voteId()))
                    .willReturn(candidates);

            // when & then
            assertThatThrownBy(
                            () -> partyRestaurantService.append(request, teamId, userId, partyId))
                    .isInstanceOf(BusinessException.class)
                    .hasMessage("후보 식당 추가는 5개가 최대입니다.");
        }

        @Test
        @DisplayName("이미 등록된 식당이면 예외를 발생시킨다.")
        void alreadyRegistered() {
            // given
            final Party party = PartyFixture.fixture(partyId, teamId);

            final Long TeamUserId = 13L;
            final Team team = TeamFixture.fixture(teamId, true);
            final TeamUser teamUser =
                    TeamUserFixture.fixture(
                            TeamUserId,
                            team,
                            userId,
                            TeamUserStatus.APPROVED,
                            TeamRole.TEAM_MEMBER,
                            true);

            given(teamUserRepository.findByUserIdAndTeamIdAndUse(userId, teamId, true))
                    .willReturn(Optional.of(teamUser));
            given(partyRepository.findByIdAndUseTrue(partyId)).willReturn(Optional.of(party));

            final Vote vote =
                    VoteFixture.fixture(VoteType.RANDOM, VoteStatus.VOTING, true, partyId);
            given(voteRepository.findByIdAndPartyIdAndUseTrue(request.voteId(), partyId))
                    .willReturn(Optional.of(vote));

            final VoteRestaurantCandidate existing =
                    VoteRestaurantCandidateFixture.fixture(1L, voteId, teamRestaurantId, true);
            given(partyRestaurantRepository.findAllByVoteIdAndUseTrueForUpdate(voteId))
                    .willReturn(List.of(existing));

            final List<VoteRestaurantCandidate> candidates =
                    List.of(
                            VoteRestaurantCandidateFixture.fixture(1L, voteId, 111L, true),
                            VoteRestaurantCandidateFixture.fixture(2L, voteId, 112L, true),
                            VoteRestaurantCandidateFixture.fixture(3L, voteId, 113L, true),
                            VoteRestaurantCandidateFixture.fixture(4L, voteId, 123L, true));
            given(partyRestaurantRepository.findAllByVoteIdAndUseTrueForUpdate(request.voteId()))
                    .willReturn(candidates);

            // when & then
            assertThatThrownBy(
                            () -> partyRestaurantService.append(request, teamId, userId, partyId))
                    .isInstanceOf(BusinessException.class)
                    .hasMessage("이미 식당이 등록되어 있습니다.");
        }
    }
}
