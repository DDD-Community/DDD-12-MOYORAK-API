package com.moyorak.api.team.service;

import com.moyorak.api.team.domain.Team;
import com.moyorak.api.team.domain.TeamRole;
import com.moyorak.api.team.domain.TeamUser;
import com.moyorak.api.team.domain.TeamUserNotFoundException;
import com.moyorak.api.team.domain.TeamUserStatus;
import com.moyorak.api.team.dto.TeamUserListRequest;
import com.moyorak.api.team.dto.TeamUserResponse;
import com.moyorak.api.team.repository.TeamRepository;
import com.moyorak.api.team.repository.TeamUserRepository;
import com.moyorak.config.exception.BusinessException;
import com.moyorak.global.domain.ListResponse;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
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

    @Transactional(readOnly = true)
    public ListResponse<TeamUserResponse> getTeamUsers(
            final Long userId, final Long teamId, final TeamUserListRequest request) {
        final TeamUser teamUser =
                teamUserRepository
                        .findByUserIdAndTeamIdAndUse(userId, teamId, true)
                        .orElseThrow(TeamUserNotFoundException::new);

        if (teamUser.isNotApproved()) {
            throw new TeamUserNotFoundException();
        }

        if (!teamUser.isTeamAdmin()) {
            throw new BusinessException("팀 관리자만 조회할 수 있습니다.");
        }

        final Page<TeamUserResponse> teamUsers =
                teamUserRepository.findByConditions(
                        teamId, request.getStatus(), true, request.toRecentPageable());

        return ListResponse.from(teamUsers);
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
