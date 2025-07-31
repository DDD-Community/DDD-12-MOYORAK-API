package com.moyorak.api.team.service;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;

import com.moyorak.api.team.domain.NotTeamAdminException;
import com.moyorak.api.team.domain.NotTeamUserException;
import com.moyorak.api.team.domain.Team;
import com.moyorak.api.team.domain.TeamFixture;
import com.moyorak.api.team.domain.TeamRole;
import com.moyorak.api.team.domain.TeamUser;
import com.moyorak.api.team.domain.TeamUserFixture;
import com.moyorak.api.team.domain.TeamUserNotFoundException;
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
class TeamUserManagementServiceTest {

    @InjectMocks private TeamUserManagementService teamUserManagementService;

    @Mock private TeamUserRepository teamUserRepository;

    @Nested
    @DisplayName("역할 변경 시")
    class UpdateRole {

        final Long teamId = 1L;
        final Long requesterUserId = 1L;
        final Long teamUserAdminId = 1L;
        final Long targetTeamUserId = 2L;

        @Test
        @DisplayName("요청자가 팀 유저가 아닌 경우 TeamUserNotFoundException이 발생한다")
        void isNotTeamUser() {
            // given
            given(teamUserRepository.findByUserIdAndTeamIdAndUse(requesterUserId, teamId, true))
                    .willReturn(Optional.empty());

            // when & then
            assertThatThrownBy(
                            () ->
                                    teamUserManagementService.updateRole(
                                            requesterUserId,
                                            teamId,
                                            targetTeamUserId,
                                            TeamRole.TEAM_ADMIN))
                    .isInstanceOf(TeamUserNotFoundException.class)
                    .hasMessage("팀 멤버가 아닙니다.");
        }

        @Test
        @DisplayName("요청자가 관리자가 아닌 경우 NotTeamAdminException이 발생한다")
        void isNotAdmin() {
            // given
            final TeamUser nonAdmin =
                    TeamUserFixture.fixture(TeamUserStatus.APPROVED, TeamRole.TEAM_MEMBER, true);
            given(teamUserRepository.findByUserIdAndTeamIdAndUse(requesterUserId, teamId, true))
                    .willReturn(Optional.of(nonAdmin));

            // when & then
            assertThatThrownBy(
                            () ->
                                    teamUserManagementService.updateRole(
                                            requesterUserId,
                                            teamId,
                                            targetTeamUserId,
                                            TeamRole.TEAM_ADMIN))
                    .isInstanceOf(NotTeamAdminException.class)
                    .hasMessage("팀 관리자가 아닙니다.");
        }

        @Test
        @DisplayName("본인을 변경하려는 경우 BusinessException이 발생한다")
        void changeSelfRole() {
            // given
            final Team team = TeamFixture.fixture(teamId, true);
            final TeamUser adminUser =
                    TeamUserFixture.fixture(
                            teamUserAdminId,
                            team,
                            requesterUserId,
                            TeamUserStatus.APPROVED,
                            TeamRole.TEAM_ADMIN,
                            true);
            given(teamUserRepository.findByUserIdAndTeamIdAndUse(requesterUserId, teamId, true))
                    .willReturn(Optional.of(adminUser));

            // when & then
            assertThatThrownBy(
                            () ->
                                    teamUserManagementService.updateRole(
                                            requesterUserId,
                                            teamId,
                                            adminUser.getId(),
                                            TeamRole.TEAM_ADMIN))
                    .isInstanceOf(BusinessException.class)
                    .hasMessage("본인은 변경이 불가능합니다.");
        }

        @Test
        @DisplayName("변경 대상이 승인 상태 유저가 아닌 경우 TeamUserNotFoundException이 발생한다")
        void targetUserNotFound() {
            // given
            final Team team = TeamFixture.fixture(teamId, true);
            final TeamUser adminUser =
                    TeamUserFixture.fixture(
                            teamUserAdminId,
                            team,
                            requesterUserId,
                            TeamUserStatus.APPROVED,
                            TeamRole.TEAM_ADMIN,
                            true);
            given(teamUserRepository.findByUserIdAndTeamIdAndUse(requesterUserId, teamId, true))
                    .willReturn(Optional.of(adminUser));
            given(
                            teamUserRepository.findByIdAndUseAndStatus(
                                    targetTeamUserId, true, TeamUserStatus.APPROVED))
                    .willReturn(Optional.empty());

            // when & then
            assertThatThrownBy(
                            () ->
                                    teamUserManagementService.updateRole(
                                            requesterUserId,
                                            teamId,
                                            targetTeamUserId,
                                            TeamRole.TEAM_MEMBER))
                    .isInstanceOf(TeamUserNotFoundException.class)
                    .hasMessage("팀 멤버가 아닙니다.");
        }

        @Test
        @DisplayName("변경 대상이 다른 팀에 속한 경우 NotTeamUserException이 발생한다")
        void isNotSameTeam() {
            // given
            final Team team = TeamFixture.fixture(teamId, true);
            final TeamUser adminUser =
                    TeamUserFixture.fixture(
                            teamUserAdminId,
                            team,
                            requesterUserId,
                            TeamUserStatus.APPROVED,
                            TeamRole.TEAM_ADMIN,
                            true);

            final Team otherTeam = TeamFixture.fixture(999L, true);
            final TeamUser otherTeamUser =
                    TeamUserFixture.fixture(
                            targetTeamUserId,
                            otherTeam,
                            2L,
                            TeamUserStatus.APPROVED,
                            TeamRole.TEAM_ADMIN,
                            true);

            given(teamUserRepository.findByUserIdAndTeamIdAndUse(requesterUserId, teamId, true))
                    .willReturn(Optional.of(adminUser));
            given(
                            teamUserRepository.findByIdAndUseAndStatus(
                                    targetTeamUserId, true, TeamUserStatus.APPROVED))
                    .willReturn(Optional.of(otherTeamUser));

            // when & then
            assertThatThrownBy(
                            () ->
                                    teamUserManagementService.updateRole(
                                            requesterUserId,
                                            teamId,
                                            targetTeamUserId,
                                            TeamRole.TEAM_MEMBER))
                    .isInstanceOf(NotTeamUserException.class)
                    .hasMessage("해당 팀의 팀원이 아닙니다.");
        }
    }
}
