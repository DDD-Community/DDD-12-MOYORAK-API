package com.moyorak.api.team.service;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;

import com.moyorak.api.team.domain.Team;
import com.moyorak.api.team.domain.TeamFixture;
import com.moyorak.api.team.domain.TeamInvitation;
import com.moyorak.api.team.domain.TeamInvitationFixture;
import com.moyorak.api.team.domain.TeamInvitationTokenNotFoundException;
import com.moyorak.api.team.domain.TeamUser;
import com.moyorak.api.team.domain.TeamUserFixture;
import com.moyorak.api.team.domain.TeamUserNotFoundException;
import com.moyorak.api.team.domain.TeamUserStatus;
import com.moyorak.api.team.repository.TeamInvitationRepository;
import com.moyorak.api.team.repository.TeamUserRepository;
import com.moyorak.config.exception.BusinessException;
import java.time.LocalDateTime;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class TeamInvitationServiceTest {

    @InjectMocks private TeamInvitationService teamInvitationService;

    @Mock private TeamUserRepository teamUserRepository;
    @Mock private TeamInvitationRepository teamInvitationRepository;

    @Nested
    @DisplayName("초대 링크 토큰 생성 시")
    class Create {

        final Long teamId = 1L;
        final Long userId = 1L;

        @Test
        @DisplayName("팀 멤버가 아닌 경우 TeamUserNotFoundException이 발생한다.")
        void noTeamUser() {
            // given
            given(teamUserRepository.findByUserIdAndTeamIdAndUse(teamId, userId, true))
                    .willReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> teamInvitationService.createTeamInvitation(teamId, userId))
                    .isInstanceOf(TeamUserNotFoundException.class)
                    .hasMessage("팀 멤버가 아닙니다.");
        }

        @Test
        @DisplayName("팀 멤버 상태가 APPROVED가 아닌 경우 예외가 발생한다.")
        void isNotApproved() {
            // given
            final Team team = TeamFixture.fixture(teamId, null, true);
            final TeamUser pendingTeamUser =
                    TeamUserFixture.fixture(userId, team, TeamUserStatus.PENDING, true);

            given(teamUserRepository.findByUserIdAndTeamIdAndUse(teamId, userId, true))
                    .willReturn(Optional.of(pendingTeamUser));

            // when & then
            assertThatThrownBy(() -> teamInvitationService.createTeamInvitation(teamId, userId))
                    .isInstanceOf(TeamUserNotFoundException.class)
                    .hasMessage("팀 멤버가 아닙니다.");
        }
    }

    @Nested
    @DisplayName("초대 링크 조회 시")
    class GetInvitationDetail {

        final String token = "sample-token";
        final Long teamId = 1L;

        @Test
        @DisplayName("토큰이 존재하지 않으면 예외가 발생한다.")
        void noToken() {
            // given
            given(teamInvitationRepository.findByInvitationToken(token))
                    .willReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> teamInvitationService.getInvitationDetail(teamId, token))
                    .isInstanceOf(TeamInvitationTokenNotFoundException.class)
                    .hasMessage("토큰이 존재하지 않습니다.");
        }

        @Test
        @DisplayName("토큰이 만료되었으면 예외가 발생한다.")
        void tokenExpired() {
            // given
            LocalDateTime expiredExpiresDate = LocalDateTime.now().minusSeconds(1);
            final TeamInvitation expiredInvitation =
                    TeamInvitationFixture.fixture(1L, token, expiredExpiresDate, teamId);

            given(teamInvitationRepository.findByInvitationToken(token))
                    .willReturn(Optional.of(expiredInvitation));

            // when & then
            assertThatThrownBy(() -> teamInvitationService.getInvitationDetail(teamId, token))
                    .isInstanceOf(BusinessException.class)
                    .hasMessage("토큰이 만료되었습니다.");
        }
    }
}
