package com.moyorak.api.team.service;

import com.moyorak.api.team.domain.Team;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class TeamJoinFacade {
    private final TeamService teamService;
    private final TeamUserService teamUserService;

    @Transactional
    public void requestJoin(final Long userId, final Long teamId) {
        final Team team = teamService.getTeam(teamId);
        teamUserService.requestJoin(userId, team);
    }
}
