package com.moyorak.api.team.service;

import com.moyorak.api.team.domain.NotTeamAdminException;
import com.moyorak.api.team.domain.NotTeamUserException;
import com.moyorak.api.team.domain.TeamRole;
import com.moyorak.api.team.domain.TeamUser;
import com.moyorak.api.team.domain.TeamUserNotFoundException;
import com.moyorak.api.team.domain.TeamUserStatus;
import com.moyorak.api.team.repository.TeamUserRepository;
import com.moyorak.config.exception.BusinessException;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class TeamUserManagementService {

    private final TeamUserRepository teamUserRepository;

    @Transactional
    public void updateRole(
            final Long userId, final Long teamId, final Long teamUserId, final TeamRole role) {
        final TeamUser teamUserAdmin =
                teamUserRepository
                        .findByUserIdAndTeamIdAndUse(userId, teamId, true)
                        .orElseThrow(TeamUserNotFoundException::new);

        if (!teamUserAdmin.isTeamAdmin()) {
            throw new NotTeamAdminException();
        }

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
}
