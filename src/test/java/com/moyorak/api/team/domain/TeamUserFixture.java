package com.moyorak.api.team.domain;

import org.springframework.test.util.ReflectionTestUtils;

public class TeamUserFixture {

    public static TeamUser fixture(
            final Long userId, final Team team, final TeamUserStatus status, boolean use) {
        TeamUser teamUser = new TeamUser();

        ReflectionTestUtils.setField(teamUser, "userId", userId);
        ReflectionTestUtils.setField(teamUser, "team", team);
        ReflectionTestUtils.setField(teamUser, "status", status);
        ReflectionTestUtils.setField(teamUser, "use", use);

        return teamUser;
    }

    public static TeamUser fixture(final TeamUserStatus status, final TeamRole role, boolean use) {
        TeamUser teamUser = new TeamUser();

        ReflectionTestUtils.setField(teamUser, "status", status);
        ReflectionTestUtils.setField(teamUser, "role", role);
        ReflectionTestUtils.setField(teamUser, "use", use);

        return teamUser;
    }

    public static TeamUser fixtureApproved(final Long userId, final Team team) {
        return fixture(userId, team, TeamUserStatus.APPROVED, true);
    }

    public static TeamUser fixture(
            final Long id,
            final Team team,
            final Long userId,
            final TeamUserStatus status,
            final TeamRole role,
            final boolean use) {
        TeamUser teamUser = new TeamUser();

        ReflectionTestUtils.setField(teamUser, "id", id);
        ReflectionTestUtils.setField(teamUser, "team", team);
        ReflectionTestUtils.setField(teamUser, "status", status);
        ReflectionTestUtils.setField(teamUser, "role", role);
        ReflectionTestUtils.setField(teamUser, "use", use);
        ReflectionTestUtils.setField(teamUser, "userId", userId);

        return teamUser;
    }
}
