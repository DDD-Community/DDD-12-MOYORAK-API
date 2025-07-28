package com.moyorak.api.team.service;

import com.moyorak.api.team.domain.TeamUser;
import com.moyorak.api.team.domain.TeamUserNotFoundException;
import com.moyorak.api.team.dto.TeamUserListRequest;
import com.moyorak.api.team.dto.TeamUserResponse;
import com.moyorak.api.team.repository.TeamUserRepository;
import com.moyorak.config.exception.BusinessException;
import com.moyorak.global.domain.ListResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
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
}
