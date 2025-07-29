package com.moyorak.api.team.service;

import com.moyorak.api.company.domain.Company;
import com.moyorak.api.company.repository.CompanyRepository;
import com.moyorak.api.team.domain.Team;
import com.moyorak.api.team.domain.TeamSearch;
import com.moyorak.api.team.dto.TeamSaveRequest;
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

    private final CompanyRepository companyRepository;

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

    /**
     * 팀을 생성합니다.
     *
     * @param companyId 회사 ID
     * @param request 팀 생성 요청 DTO
     */
    @Transactional
    public Long create(final Long companyId, final TeamSaveRequest request) {
        final Company company =
                companyRepository
                        .findById(companyId)
                        .orElseThrow(() -> new BusinessException("회사를 찾을 수 없습니다."));

        // TODO: 회사에 속해있는 회원은 검증 안해도 되련지?

        if (teamRepository.existsByCompanyAndName(company, request.name())) {
            throw new BusinessException("이미 존재하는 팀 이름입니다.");
        }

        return teamRepository.save(request.toEntity(company)).getId();
    }
}
