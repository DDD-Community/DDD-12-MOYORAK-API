package com.moyorak.api.team.service;

import com.moyorak.api.team.domain.Team;
import com.moyorak.api.team.domain.TeamSearch;
import com.moyorak.api.team.dto.TeamSearchListResponse;
import com.moyorak.api.team.dto.TeamSearchRequest;
import com.moyorak.api.team.repository.TeamRepository;
import com.moyorak.api.team.repository.TeamSearchRepository;
import com.moyorak.config.exception.BusinessException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class TeamService {

    private final TeamSearchRepository teamSearchRepository;
    private final TeamRepository teamRepository;

    @Transactional(readOnly = true)
    public TeamSearchListResponse search(final Long companyId, final TeamSearchRequest request) {
        final List<TeamSearch> teams =
                teamSearchRepository.findByConditions(companyId, request.teamId(), request.name());

        return TeamSearchListResponse.from(teams);
    }

    @Transactional(readOnly = true)
    public Team getTeam(final Long teamId) {
        return teamRepository
                .findById(teamId)
                .orElseThrow(() -> new BusinessException("팀이 존재 하지 않습니다."));
    }
}
