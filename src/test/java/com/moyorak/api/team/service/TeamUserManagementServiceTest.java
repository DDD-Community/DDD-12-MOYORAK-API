package com.moyorak.api.team.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;

import com.moyorak.api.company.domain.Company;
import com.moyorak.api.company.domain.CompanyFixture;
import com.moyorak.api.team.domain.NotTeamAdminException;
import com.moyorak.api.team.domain.NotTeamUserException;
import com.moyorak.api.team.domain.Team;
import com.moyorak.api.team.domain.TeamFixture;
import com.moyorak.api.team.domain.TeamRole;
import com.moyorak.api.team.domain.TeamUser;
import com.moyorak.api.team.domain.TeamUserFixture;
import com.moyorak.api.team.domain.TeamUserNotFoundException;
import com.moyorak.api.team.domain.TeamUserStatus;
import com.moyorak.api.team.dto.TeamUserListRequest;
import com.moyorak.api.team.dto.TeamUserListRequestFixture;
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
    @DisplayName("역할 양도 시")
    class transferManager {

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
                                    teamUserManagementService.transferManagerRole(
                                            requesterUserId, teamId, targetTeamUserId))
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
                                    teamUserManagementService.transferManagerRole(
                                            requesterUserId, teamId, targetTeamUserId))
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
                                    teamUserManagementService.transferManagerRole(
                                            requesterUserId, teamId, adminUser.getId()))
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
                                    teamUserManagementService.transferManagerRole(
                                            requesterUserId, teamId, targetTeamUserId))
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
                                    teamUserManagementService.transferManagerRole(
                                            requesterUserId, teamId, targetTeamUserId))
                    .isInstanceOf(NotTeamUserException.class)
                    .hasMessage("해당 팀의 팀원이 아닙니다.");
        }
    }

    @Nested
    @DisplayName("getTeamUsers 호출 시")
    class GetTeamUsers {

        final Long teamId = 1L;
        final Long userId = 1L;

        @Test
        @DisplayName("팀 유저가 존재하지 않으면 TeamUserNotFoundException이 발생한다.")
        void isNotExist() {
            // given
            final TeamUserListRequest request =
                    TeamUserListRequestFixture.fixture(TeamUserStatus.APPROVED, 10, 1);
            given(teamUserRepository.findByUserIdAndTeamIdAndUse(userId, teamId, true))
                    .willReturn(Optional.empty());

            // when & then
            assertThatThrownBy(
                            () -> teamUserManagementService.getTeamUsers(userId, teamId, request))
                    .isInstanceOf(TeamUserNotFoundException.class)
                    .hasMessage("팀 멤버가 아닙니다.");
        }

        @Test
        @DisplayName("팀 유저가 가입 상태가 아니면 TeamUserNotFoundException이 발생한다.")
        void isNotApproved() {
            // given
            final TeamUser notApprovedUser =
                    TeamUserFixture.fixture(TeamUserStatus.PENDING, TeamRole.TEAM_MEMBER, true);
            final TeamUserListRequest request =
                    TeamUserListRequestFixture.fixture(TeamUserStatus.APPROVED, 10, 1);

            given(teamUserRepository.findByUserIdAndTeamIdAndUse(userId, teamId, true))
                    .willReturn(Optional.of(notApprovedUser));

            // when & then
            assertThatThrownBy(
                            () -> teamUserManagementService.getTeamUsers(userId, teamId, request))
                    .isInstanceOf(TeamUserNotFoundException.class)
                    .hasMessage("팀 멤버가 아닙니다.");
        }

        @Test
        @DisplayName("팀 관리자가 아닌 경우 BusinessException이 발생한다.")
        void isNotAdmin() {
            // given
            final TeamUser normalUser =
                    TeamUserFixture.fixture(TeamUserStatus.APPROVED, TeamRole.TEAM_MEMBER, true);
            final TeamUserListRequest request =
                    TeamUserListRequestFixture.fixture(
                            TeamUserStatus.APPROVED, 10, 1); // 가입 아닌 상태 조회 시도

            given(teamUserRepository.findByUserIdAndTeamIdAndUse(userId, teamId, true))
                    .willReturn(Optional.of(normalUser));

            // when & then
            assertThatThrownBy(
                            () -> teamUserManagementService.getTeamUsers(userId, teamId, request))
                    .isInstanceOf(NotTeamAdminException.class)
                    .hasMessage("팀 관리자가 아닙니다.");
        }
    }

    @Nested
    @DisplayName("가입 신청 승인 시")
    class ApproveRequestJoin {

        final Long teamId = 200L;
        final Long otherTeamId = 300L;
        final Long adminUserId = 10L;
        final Long pendingMemberId = 20L;
        final Company company = CompanyFixture.fixture(1L, "우가우가", 37.5, 36.5, true);
        final Team team = TeamFixture.fixture(teamId, company, true);
        final Team otherTeam = TeamFixture.fixture(otherTeamId, company, true);

        @Test
        @DisplayName("승인자가 팀 관리자가 아니면 예외를 발생시킨다")
        void notAdminUser() {
            // given
            final TeamUser nonAdminUser =
                    TeamUserFixture.fixture(TeamUserStatus.APPROVED, TeamRole.TEAM_MEMBER, true);
            given(teamUserRepository.findByUserIdAndTeamIdAndUse(adminUserId, teamId, true))
                    .willReturn(Optional.of(nonAdminUser));

            // when & then
            assertThatThrownBy(
                            () ->
                                    teamUserManagementService.approveRequestJoin(
                                            adminUserId, teamId, pendingMemberId))
                    .isInstanceOf(NotTeamAdminException.class)
                    .hasMessage("팀 관리자가 아닙니다.");
        }

        @Test
        @DisplayName("요청자가 팀원이 아니면 예외를 발생시킨다")
        void notSameTeam() {
            // given
            final TeamUser adminUser =
                    TeamUserFixture.fixture(TeamUserStatus.APPROVED, TeamRole.TEAM_ADMIN, true);
            final TeamUser otherTeamUser =
                    TeamUserFixture.fixture(
                            pendingMemberId, otherTeam, TeamUserStatus.PENDING, true);
            given(teamUserRepository.findByUserIdAndTeamIdAndUse(adminUserId, teamId, true))
                    .willReturn(Optional.of(adminUser));
            given(
                            teamUserRepository.findByIdAndUseAndStatus(
                                    pendingMemberId, true, TeamUserStatus.PENDING))
                    .willReturn(Optional.of(otherTeamUser));

            // when & then
            assertThatThrownBy(
                            () ->
                                    teamUserManagementService.approveRequestJoin(
                                            adminUserId, teamId, pendingMemberId))
                    .isInstanceOf(BusinessException.class)
                    .hasMessage("해당 팀의 팀원이 아닙니다.");
        }

        @Test
        @DisplayName("승인이 정상적으로 처리된다")
        void success() {
            // given
            final TeamUser adminUser =
                    TeamUserFixture.fixture(TeamUserStatus.APPROVED, TeamRole.TEAM_ADMIN, true);
            final TeamUser pendingUser =
                    TeamUserFixture.fixture(pendingMemberId, team, TeamUserStatus.PENDING, true);
            given(teamUserRepository.findByUserIdAndTeamIdAndUse(adminUserId, teamId, true))
                    .willReturn(Optional.of(adminUser));
            given(
                            teamUserRepository.findByIdAndUseAndStatus(
                                    pendingMemberId, true, TeamUserStatus.PENDING))
                    .willReturn(Optional.of(pendingUser));

            // when
            teamUserManagementService.approveRequestJoin(adminUserId, teamId, pendingMemberId);

            // then
            assertThat(pendingUser.getStatus()).isEqualTo(TeamUserStatus.APPROVED);
        }
    }

    @Nested
    @DisplayName("expel 호출 시")
    class Expel {

        final Long teamId = 1L;
        final Long requesterUserId = 3L;
        final Long targetTeamUserId = 20L;

        @Test
        @DisplayName("요청한 사용자가 팀원이 아닌 경우 TeamUserNotFoundException 발생")
        void adminNotTeamUser() {
            // given
            given(teamUserRepository.findByUserIdAndTeamIdAndUse(requesterUserId, teamId, true))
                    .willReturn(Optional.empty());

            // when & then
            assertThatThrownBy(
                            () ->
                                    teamUserManagementService.expel(
                                            requesterUserId, teamId, targetTeamUserId))
                    .isInstanceOf(TeamUserNotFoundException.class)
                    .hasMessage("팀 멤버가 아닙니다.");
        }

        @Test
        @DisplayName("요청한 사용자가 APPROVED 상태가 아닌 경우 TeamUserNotFoundException 발생")
        void adminNotApproved() {
            // given
            final TeamUser withdrawnAdmin =
                    TeamUserFixture.fixture(TeamUserStatus.WITHDRAWN, TeamRole.TEAM_ADMIN, true);

            given(teamUserRepository.findByUserIdAndTeamIdAndUse(requesterUserId, teamId, true))
                    .willReturn(Optional.of(withdrawnAdmin));

            // when & then
            assertThatThrownBy(
                            () ->
                                    teamUserManagementService.expel(
                                            requesterUserId, teamId, targetTeamUserId))
                    .isInstanceOf(TeamUserNotFoundException.class)
                    .hasMessage("팀 멤버가 아닙니다.");
        }

        @Test
        @DisplayName("요청한 사용자가 관리자 권한이 아닌 경우 NotTeamAdminException 발생")
        void notAdmin() {
            // given
            final TeamUser teamMember =
                    TeamUserFixture.fixture(TeamUserStatus.APPROVED, TeamRole.TEAM_MEMBER, true);

            given(teamUserRepository.findByUserIdAndTeamIdAndUse(requesterUserId, teamId, true))
                    .willReturn(Optional.of(teamMember));

            // when & then
            assertThatThrownBy(
                            () ->
                                    teamUserManagementService.expel(
                                            requesterUserId, teamId, targetTeamUserId))
                    .isInstanceOf(NotTeamAdminException.class)
                    .hasMessage("팀 관리자가 아닙니다.");
        }

        @Test
        @DisplayName("자기 자신을 강퇴하려는 경우 BusinessException 발생")
        void expelSelf() {
            // given
            final Team team = TeamFixture.fixture(teamId, true);
            final TeamUser admin =
                    TeamUserFixture.fixture(
                            targetTeamUserId,
                            team,
                            requesterUserId,
                            TeamUserStatus.APPROVED,
                            TeamRole.TEAM_ADMIN,
                            true);

            given(teamUserRepository.findByUserIdAndTeamIdAndUse(requesterUserId, teamId, true))
                    .willReturn(Optional.of(admin));

            // when & then
            assertThatThrownBy(
                            () ->
                                    teamUserManagementService.expel(
                                            requesterUserId, teamId, targetTeamUserId))
                    .isInstanceOf(BusinessException.class)
                    .hasMessage("본인을 강퇴할 수 없습니다.");
        }

        @Test
        @DisplayName("대상 유저가 존재하지 않으면 NotTeamUserException 발생")
        void targetNotFound() {
            // given
            final Team team = TeamFixture.fixture(teamId, true);
            final TeamUser admin =
                    TeamUserFixture.fixture(
                            1L,
                            team,
                            requesterUserId,
                            TeamUserStatus.APPROVED,
                            TeamRole.TEAM_ADMIN,
                            true);

            given(teamUserRepository.findByUserIdAndTeamIdAndUse(requesterUserId, teamId, true))
                    .willReturn(Optional.of(admin));
            given(
                            teamUserRepository.findByIdAndUseAndStatus(
                                    targetTeamUserId, true, TeamUserStatus.APPROVED))
                    .willReturn(Optional.empty());

            // when & then
            assertThatThrownBy(
                            () ->
                                    teamUserManagementService.expel(
                                            requesterUserId, teamId, targetTeamUserId))
                    .isInstanceOf(TeamUserNotFoundException.class)
                    .hasMessage("팀 멤버가 아닙니다.");
        }

        @Test
        @DisplayName("대상 유저가 다른 팀 소속일 경우 BusinessException 발생")
        void targetNotSameTeam() {
            // given
            final Team team = TeamFixture.fixture(teamId, true);
            final TeamUser admin =
                    TeamUserFixture.fixture(
                            1L,
                            team,
                            requesterUserId,
                            TeamUserStatus.APPROVED,
                            TeamRole.TEAM_ADMIN,
                            true);

            final Team otherTeam = TeamFixture.fixture(99L, true);
            final TeamUser target =
                    TeamUserFixture.fixture(
                            targetTeamUserId,
                            otherTeam,
                            requesterUserId,
                            TeamUserStatus.APPROVED,
                            TeamRole.TEAM_MEMBER,
                            true);

            given(teamUserRepository.findByUserIdAndTeamIdAndUse(requesterUserId, teamId, true))
                    .willReturn(Optional.of(admin));
            given(
                            teamUserRepository.findByIdAndUseAndStatus(
                                    targetTeamUserId, true, TeamUserStatus.APPROVED))
                    .willReturn(Optional.of(target));

            // when & then
            assertThatThrownBy(
                            () ->
                                    teamUserManagementService.expel(
                                            requesterUserId, teamId, targetTeamUserId))
                    .isInstanceOf(NotTeamUserException.class)
                    .hasMessage("해당 팀의 팀원이 아닙니다.");
        }
    }

    @Nested
    @DisplayName("가입 신청 거절 시")
    class RejectRequestJoin {

        final Long teamId = 200L;
        final Long otherTeamId = 300L;
        final Long adminUserId = 10L;
        final Long pendingMemberId = 20L;
        final Company company = CompanyFixture.fixture(1L, "우가우가", 37.5, 36.5, true);
        final Team team = TeamFixture.fixture(teamId, company, true);
        final Team otherTeam = TeamFixture.fixture(otherTeamId, company, true);

        @Test
        @DisplayName("승인자가 팀 관리자가 아니면 예외를 발생시킨다")
        void notAdminUser() {
            // given
            final TeamUser nonAdminUser =
                    TeamUserFixture.fixture(TeamUserStatus.APPROVED, TeamRole.TEAM_MEMBER, true);
            given(teamUserRepository.findByUserIdAndTeamIdAndUse(adminUserId, teamId, true))
                    .willReturn(Optional.of(nonAdminUser));

            // when & then
            assertThatThrownBy(
                            () ->
                                    teamUserManagementService.rejectRequestJoin(
                                            adminUserId, teamId, pendingMemberId))
                    .isInstanceOf(NotTeamAdminException.class)
                    .hasMessage("팀 관리자가 아닙니다.");
        }

        @Test
        @DisplayName("요청자가 팀원이 아니면 예외를 발생시킨다")
        void notSameTeam() {
            // given
            final TeamUser adminUser =
                    TeamUserFixture.fixture(TeamUserStatus.APPROVED, TeamRole.TEAM_ADMIN, true);
            final TeamUser otherTeamUser =
                    TeamUserFixture.fixture(
                            pendingMemberId, otherTeam, TeamUserStatus.PENDING, true);
            given(teamUserRepository.findByUserIdAndTeamIdAndUse(adminUserId, teamId, true))
                    .willReturn(Optional.of(adminUser));
            given(
                            teamUserRepository.findByIdAndUseAndStatus(
                                    pendingMemberId, true, TeamUserStatus.PENDING))
                    .willReturn(Optional.of(otherTeamUser));

            // when & then
            assertThatThrownBy(
                            () ->
                                    teamUserManagementService.rejectRequestJoin(
                                            adminUserId, teamId, pendingMemberId))
                    .isInstanceOf(BusinessException.class)
                    .hasMessage("해당 팀의 팀원이 아닙니다.");
        }

        @Test
        @DisplayName("거절이 정상적으로 처리된다")
        void success() {
            // given
            final TeamUser adminUser =
                    TeamUserFixture.fixture(TeamUserStatus.APPROVED, TeamRole.TEAM_ADMIN, true);
            final TeamUser pendingUser =
                    TeamUserFixture.fixture(pendingMemberId, team, TeamUserStatus.PENDING, true);
            given(teamUserRepository.findByUserIdAndTeamIdAndUse(adminUserId, teamId, true))
                    .willReturn(Optional.of(adminUser));
            given(
                            teamUserRepository.findByIdAndUseAndStatus(
                                    pendingMemberId, true, TeamUserStatus.PENDING))
                    .willReturn(Optional.of(pendingUser));

            // when
            teamUserManagementService.rejectRequestJoin(adminUserId, teamId, pendingMemberId);

            // then
            assertThat(pendingUser.getStatus()).isEqualTo(TeamUserStatus.REJECTED);
        }
    }
}
