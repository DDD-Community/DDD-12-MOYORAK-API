package com.moyorak.api.team.service;

import com.moyorak.api.team.dto.TeamSaveRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class TeamFacade {

    private final TeamService teamService;

    private final TeamUserService teamUserService;

    /**
     * 팀을 생성합니다. 팀 생성 시 최초 생성자는 팀장이 됩니다.
     *
     * @param companyId 회사 ID
     * @param request 팀 생성 요청 DTO
     */
    @Transactional
    public void create(final Long companyId, final Long userId, final TeamSaveRequest request) {
        // 1. 이미 다른 팀에 소속중인지 확인
        teamUserService.assignUserToTeam(userId);

        // 2. 팀 생성
        final Long teamId = teamService.create(companyId, request);

        // 3. 팀에 관리자로 소속 추가
        teamUserService.adminJoin(teamId, userId);
    }
}
