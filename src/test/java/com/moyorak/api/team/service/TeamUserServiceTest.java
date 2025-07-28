package com.moyorak.api.team.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.mock;

import com.moyorak.api.company.domain.Company;
import com.moyorak.api.company.domain.CompanyFixture;
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
class TeamUserServiceTest {

    @InjectMocks private TeamUserService teamUserService;

    @Mock private TeamUserRepository teamUserRepository;

    @Nested
    @DisplayName("withdraw 호출 시")
    class Withdraw {

        final Long teamId = 1L;
        final Long userId = 1L;

        @Test
        @DisplayName("팀유저가 아닌 경우 TeamUserNotFoundException이 발생한다.")
        void isNotExist() {
            // given
            given(teamUserRepository.findByUserIdAndTeamIdAndUse(userId, teamId, true))
                    .willReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> teamUserService.withdraw(userId, teamId))
                    .isInstanceOf(TeamUserNotFoundException.class)
                    .hasMessage("팀 멤버가 아닙니다.");
        }

        @Test
        @DisplayName("팀유저 상태가 APPROVED가 아닌 경우 TeamUserNotFoundException이 발생한다.")
        void isNotApproved() {
            // given
            final TeamUser pendingUser =
                    TeamUserFixture.fixture(TeamUserStatus.PENDING, TeamRole.TEAM_MEMBER, true);

            given(teamUserRepository.findByUserIdAndTeamIdAndUse(userId, teamId, true))
                    .willReturn(Optional.of(pendingUser));

            // when & then
            assertThatThrownBy(() -> teamUserService.withdraw(userId, teamId))
                    .isInstanceOf(TeamUserNotFoundException.class)
                    .hasMessage("팀 멤버가 아닙니다.");
        }

        @Test
        @DisplayName("팀 관리자인 경우 BusinessException이 발생한다.")
        void isAdmin() {
            // given
            final TeamUser teamAdminUser =
                    TeamUserFixture.fixture(TeamUserStatus.APPROVED, TeamRole.TEAM_ADMIN, true);

            given(teamUserRepository.findByUserIdAndTeamIdAndUse(userId, teamId, true))
                    .willReturn(Optional.of(teamAdminUser));

            // when & then
            assertThatThrownBy(() -> teamUserService.withdraw(userId, teamId))
                    .isInstanceOf(BusinessException.class)
                    .hasMessage("팀 관리자는 탈퇴할 수 없습니다.");
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
            assertThatThrownBy(() -> teamUserService.getTeamUsers(userId, teamId, request))
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
            assertThatThrownBy(() -> teamUserService.getTeamUsers(userId, teamId, request))
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
            assertThatThrownBy(() -> teamUserService.getTeamUsers(userId, teamId, request))
                    .isInstanceOf(BusinessException.class)
                    .hasMessage("팀 관리자만 조회할 수 있습니다.");
        }
    }

    @Nested
    @DisplayName("가입 승인 신청 시")
    class RequestJoin {

        final Long userId = 1L;
        final Long teamId = 100L;
        final Company company = CompanyFixture.fixture(1L, "우가우가", 37.5, 36.5, true);
        final Team team = TeamFixture.fixture(teamId, company, true);

        @Test
        @DisplayName("기존 탈퇴한 기록이 존재하면 복원 처리한다")
        void restoreWithdrawnUser() {
            // given
            final TeamUser withdrawnUser =
                    TeamUserFixture.fixture(TeamUserStatus.WITHDRAWN, TeamRole.TEAM_MEMBER, false);
            given(teamUserRepository.findByUserIdAndTeamIdAndUse(userId, teamId, false))
                    .willReturn(Optional.of(withdrawnUser));

            // when
            teamUserService.requestJoin(userId, team);

            // then
            assertThat(withdrawnUser.isUse()).isTrue();
            assertThat(withdrawnUser.getStatus()).isEqualTo(TeamUserStatus.PENDING);
        }

        @Test
        @DisplayName("탈퇴한 기록이 없다면 새로운 대기 상태의 팀멤버를 생성한다")
        void createNewPendingUser() {
            // given
            given(teamUserRepository.findByUserIdAndTeamIdAndUse(userId, teamId, false))
                    .willReturn(Optional.empty());
            given(teamUserRepository.save(any())).willReturn(mock(TeamUser.class));

            // when
            teamUserService.requestJoin(userId, team);

            // then
            then(teamUserRepository)
                    .should()
                    .save(
                            argThat(
                                    user ->
                                            user.getUserId().equals(userId)
                                                    && user.getTeam().equals(team)
                                                    && user.getRole() == TeamRole.TEAM_MEMBER
                                                    && user.getStatus() == TeamUserStatus.PENDING));
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
                                    teamUserService.approveRequestJoin(
                                            adminUserId, teamId, pendingMemberId))
                    .isInstanceOf(BusinessException.class)
                    .hasMessage("승인을 하는 주체가, 팀 관리자가 아닙니다.");
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
                                    teamUserService.approveRequestJoin(
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
            teamUserService.approveRequestJoin(adminUserId, teamId, pendingMemberId);

            // then
            assertThat(pendingUser.getStatus()).isEqualTo(TeamUserStatus.APPROVED);
        }
    }
}
