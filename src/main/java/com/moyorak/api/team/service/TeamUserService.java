package com.moyorak.api.team.service;

import com.moyorak.api.team.domain.Team;
import com.moyorak.api.team.domain.TeamRole;
import com.moyorak.api.team.domain.TeamUser;
import com.moyorak.api.team.domain.TeamUserNotFoundException;
import com.moyorak.api.team.domain.TeamUserStatus;
import com.moyorak.api.team.repository.TeamUserRepository;
import com.moyorak.config.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class TeamUserService {

    private final TeamUserRepository teamUserRepository;

    @Transactional
    public void withdraw(final Long userId, final Long teamId) {
        final TeamUser teamUser =
                teamUserRepository
                        .findByUserIdAndTeamIdAndUse(userId, teamId, true)
                        .orElseThrow(TeamUserNotFoundException::new);

        if (teamUser.isNotApproved()) {
            throw new TeamUserNotFoundException();
        }

        if (teamUser.isTeamAdmin()) {
            throw new BusinessException("팀 관리자는 탈퇴할 수 없습니다.");
        }

        teamUser.withdraw();
    }

    @Transactional
    public void requestJoin(final Long userId, final Team team) {
        teamUserRepository.save(
                TeamUser.create(team, userId, TeamRole.TEAM_MEMBER, TeamUserStatus.PENDING));
    }

    @Transactional
    public void approveRequestJoin(final Long userId, final Long teamId, final Long teamMemberId) {
        final TeamUser teamAdminUser =
                teamUserRepository
                        .findByUserIdAndTeamIdAndUse(userId, teamId, true)
                        .orElseThrow(TeamUserNotFoundException::new);
        if (!teamAdminUser.isTeamAdmin()) {
            throw new BusinessException("승인을 하는 주체가, 팀 관리자가 아닙니다.");
        }

        final TeamUser teamUser =
                teamUserRepository
                        .findByIdAndUseAndStatus(teamMemberId, true, TeamUserStatus.PENDING)
                        .orElseThrow(TeamUserNotFoundException::new);
        if (!teamUser.isTeam(teamId)) {
            throw new BusinessException("해당 팀의 팀원이 아닙니다.");
        }

        teamUser.changeStatus(TeamUserStatus.APPROVED);
    }
}
