package com.moyorak.api.team.service;

import com.moyorak.api.team.domain.NotTeamAdminException;
import com.moyorak.api.team.domain.NotTeamUserException;
import com.moyorak.api.team.domain.TeamRole;
import com.moyorak.api.team.domain.TeamUser;
import com.moyorak.api.team.domain.TeamUserNotFoundException;
import com.moyorak.api.team.domain.TeamUserStatus;
import com.moyorak.api.team.dto.TeamUserListRequest;
import com.moyorak.api.team.dto.TeamUserResponse;
import com.moyorak.api.team.repository.TeamUserRepository;
import com.moyorak.config.exception.BusinessException;
import com.moyorak.global.domain.ListResponse;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class TeamUserManagementService {

    private final TeamUserRepository teamUserRepository;

    @Transactional
    public void updateRole(
            final Long userId, final Long teamId, final Long teamUserId, final TeamRole role) {
        final TeamUser teamUserAdmin = getTeamUser(userId, teamId);

        validateApprovedTeamUserAdmin(teamUserAdmin);

        if (Objects.equals(teamUserAdmin.getId(), teamUserId)) {
            throw new BusinessException("본인은 변경이 불가능합니다.");
        }

        final TeamUser teamUser =
                teamUserRepository
                        .findByIdAndUseAndStatus(teamUserId, true, TeamUserStatus.APPROVED)
                        .orElseThrow(TeamUserNotFoundException::new);

        if (!teamUser.isTeam(teamId)) {
            throw new NotTeamUserException();
        }

        teamUser.changeRole(role);
    }

    @Transactional(readOnly = true)
    public ListResponse<TeamUserResponse> getTeamUsers(
            final Long userId, final Long teamId, final TeamUserListRequest request) {

        TeamUser teamUserAdmin = getTeamUser(userId, teamId);

        validateApprovedTeamUserAdmin(teamUserAdmin);

        final Page<TeamUserResponse> teamUsers =
                teamUserRepository.findByConditions(
                        teamId, request.getStatus(), true, request.toRecentPageable());

        return ListResponse.from(teamUsers);
    }

    @Transactional
    public void approveRequestJoin(final Long userId, final Long teamId, final Long teamMemberId) {

        TeamUser teamUserAdmin = getTeamUser(userId, teamId);

        validateApprovedTeamUserAdmin(teamUserAdmin);

        final TeamUser teamUser =
                teamUserRepository
                        .findByIdAndUseAndStatus(teamMemberId, true, TeamUserStatus.PENDING)
                        .orElseThrow(TeamUserNotFoundException::new);
        if (!teamUser.isTeam(teamId)) {
            throw new BusinessException("해당 팀의 팀원이 아닙니다.");
        }

        teamUser.changeStatus(TeamUserStatus.APPROVED);
    }

    private TeamUser getTeamUser(final Long userId, final Long teamId) {
        return teamUserRepository
                .findByUserIdAndTeamIdAndUse(userId, teamId, true)
                .orElseThrow(TeamUserNotFoundException::new);
    }

    private void validateApprovedTeamUserAdmin(final TeamUser teamUserAdmin) {
        if (teamUserAdmin.isNotApproved()) {
            throw new TeamUserNotFoundException();
        }

        if (!teamUserAdmin.isTeamAdmin()) {
            throw new NotTeamAdminException();
        }
    }
}
