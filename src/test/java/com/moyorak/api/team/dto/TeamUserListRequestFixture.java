package com.moyorak.api.team.dto;

import com.moyorak.api.team.domain.TeamUserStatus;

public class TeamUserListRequestFixture {

    public static TeamUserListRequest fixture(
            final TeamUserStatus status, final int size, final int currentPage) {

        return new TeamUserListRequest(status, size, currentPage);
    }
}
