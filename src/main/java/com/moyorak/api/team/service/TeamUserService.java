package com.moyorak.api.team.service;

import com.moyorak.api.team.domain.Team;
import com.moyorak.api.team.domain.TeamRole;
import com.moyorak.api.team.domain.TeamUser;
import com.moyorak.api.team.domain.TeamUserNotFoundException;
import com.moyorak.api.team.domain.TeamUserStatus;
import com.moyorak.api.team.repository.TeamRepository;
import com.moyorak.api.team.repository.TeamUserRepository;
import com.moyorak.config.exception.BusinessException;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class TeamUserService {

    private final TeamUserRepository teamUserRepository;

    private final TeamRepository teamRepository;

    /**
     * 이미 다른 팀에 소속되어 있는지 확인합니다.
     *
     * @param userId
     */
    @Transactional(readOnly = true)
    public void assignUserToTeam(final Long userId) {
        if (teamUserRepository.existsByUserIdAndUseIsTrue(userId)) {
            throw new IllegalStateException("이미 다른 팀에 소속되어 있습니다.");
        }
    }

    /**
     * 팀에 사용자를 관리자로 추가합니다. 최초 생성자는 팀장이 됩니다.
     *
     * @param teamId 팀 ID
     * @param userId 사용자 ID
     */
    @Transactional
    public void adminJoin(final Long teamId, final Long userId) {
        final Team team =
                teamRepository
                        .findById(teamId)
                        .orElseThrow(() -> new BusinessException("팀을 찾을 수 없습니다."));

        teamUserRepository.save(TeamUser.firstJoin(team, userId));
    }

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
        final Optional<TeamUser> isWithdrawnTeamUser =
                teamUserRepository.findByUserIdAndTeamIdAndUse(userId, team.getId(), false);
        if (isWithdrawnTeamUser.isPresent()) {
            final TeamUser teamUser = isWithdrawnTeamUser.get();
            teamUser.restore();
        } else {
            teamUserRepository.save(
                    TeamUser.create(team, userId, TeamRole.TEAM_MEMBER, TeamUserStatus.PENDING));
        }
    }
}
