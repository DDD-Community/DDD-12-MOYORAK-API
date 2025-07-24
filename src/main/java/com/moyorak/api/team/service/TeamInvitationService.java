package com.moyorak.api.team.service;

import com.moyorak.api.team.domain.InvitationToken;
import com.moyorak.api.team.domain.TeamInvitation;
import com.moyorak.api.team.domain.TeamInvitationTokenNotFoundException;
import com.moyorak.api.team.domain.TeamUser;
import com.moyorak.api.team.domain.TeamUserNotFoundException;
import com.moyorak.api.team.dto.TeamInvitationCreateResponse;
import com.moyorak.api.team.dto.TeamInvitationDetailResponse;
import com.moyorak.api.team.repository.TeamInvitationRepository;
import com.moyorak.api.team.repository.TeamUserRepository;
import com.moyorak.config.exception.BusinessException;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class TeamInvitationService {

    private final TeamInvitationRepository teamInvitationRepository;
    private final TeamUserRepository teamUserRepository;

    @Transactional
    public TeamInvitationCreateResponse createTeamInvitation(final Long teamId, final Long userId) {

        // 팀원인지 체크
        final TeamUser teamUser =
                teamUserRepository
                        .findByUserIdAndTeamIdAndUse(teamId, userId, true)
                        .orElseThrow(TeamUserNotFoundException::new);
        if (teamUser.isNotApproved()) {
            throw new TeamUserNotFoundException();
        }

        // 초대 토큰 생성
        InvitationToken invitationToken = InvitationToken.generate();

        final TeamInvitation teamInvitation =
                TeamInvitation.create(
                        invitationToken.token(), invitationToken.expiresDate(), teamId);
        teamInvitationRepository.save(teamInvitation);

        return TeamInvitationCreateResponse.of(teamInvitation.getInvitationToken());
    }

    @Transactional(readOnly = true)
    public TeamInvitationDetailResponse getInvitationDetail(
            final Long teamId, final String invitationToken) {
        final TeamInvitation teamInvitation =
                teamInvitationRepository
                        .findByInvitationToken(invitationToken)
                        .orElseThrow(TeamInvitationTokenNotFoundException::new);

        if (teamInvitation.isNotInTeam(teamId)) {
            throw new TeamInvitationTokenNotFoundException();
        }

        if (teamInvitation.isExpired(LocalDateTime.now())) {
            throw new BusinessException("토큰이 만료되었습니다.");
        }

        return teamInvitationRepository
                .findInvitationDetailByTeamId(teamInvitation.getTeamId(), true)
                .orElseThrow(() -> new BusinessException("팀이 존재하지 않습니다."));
    }
}
