package com.moyorak.api.team.domain;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class TeamUserTest {

    @Nested
    @DisplayName("isNotApproved 메서드는")
    class IsNotApproved {

        @Test
        @DisplayName("status가 APPROVED가 아니면 true를 반환한다.")
        void isTrue() {
            // given
            final TeamUser user =
                    TeamUserFixture.fixture(TeamUserStatus.PENDING, TeamRole.TEAM_MEMBER, true);

            // when
            final boolean result = user.isNotApproved();

            // then
            assertThat(result).isTrue();
        }

        @Test
        @DisplayName("status가 APPROVED이면 false를 반환한다.")
        void isFalse() {
            // given
            final TeamUser user =
                    TeamUserFixture.fixture(TeamUserStatus.APPROVED, TeamRole.TEAM_MEMBER, true);

            // when
            final boolean result = user.isNotApproved();

            // then
            assertThat(result).isFalse();
        }
    }

    @Nested
    @DisplayName("isTeamAdmin 메서드는")
    class IsTeamAdmin {

        @Test
        @DisplayName("role이 TEAM_ADMIN이면 true를 반환한다.")
        void isTrue() {
            // given
            final TeamUser user =
                    TeamUserFixture.fixture(TeamUserStatus.APPROVED, TeamRole.TEAM_ADMIN, true);

            // when
            final boolean result = user.isTeamAdmin();

            // then
            assertThat(result).isTrue();
        }

        @Test
        @DisplayName("role이 TEAM_ADMIN이 아니면 false를 반환한다.")
        void isFalse() {
            // given
            final TeamUser user =
                    TeamUserFixture.fixture(TeamUserStatus.APPROVED, TeamRole.TEAM_MEMBER, true);

            // when
            final boolean result = user.isTeamAdmin();

            // then
            assertThat(result).isFalse();
        }
    }

    @Nested
    @DisplayName("withdraw 메서드는")
    class Withdraw {

        @Test
        @DisplayName("status를 WITHDRAWN으로, use를 false로 변경한다.")
        void updateStatusAndUse() {
            // given
            final TeamUser user =
                    TeamUserFixture.fixture(TeamUserStatus.APPROVED, TeamRole.TEAM_ADMIN, true);

            // when
            user.withdraw();

            // then
            assertThat(user.isUse()).isFalse();
            assertThat(user.getStatus()).isEqualTo(TeamUserStatus.WITHDRAWN);
        }
    }

    @Nested
    @DisplayName("expel 메서드는")
    class Expel {

        @Test
        @DisplayName("status를 WITHDRAWN으로, use를 false로 변경한다.")
        void updateStatusAndUse() {
            // given
            final TeamUser user =
                    TeamUserFixture.fixture(TeamUserStatus.APPROVED, TeamRole.TEAM_ADMIN, true);

            // when
            user.expel();

            // then
            assertThat(user.isUse()).isFalse();
            assertThat(user.getStatus()).isEqualTo(TeamUserStatus.WITHDRAWN);
        }
    }
}
