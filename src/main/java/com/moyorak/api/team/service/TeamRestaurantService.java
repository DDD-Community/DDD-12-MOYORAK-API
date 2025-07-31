package com.moyorak.api.team.service;

import com.moyorak.api.restaurant.domain.Restaurant;
import com.moyorak.api.restaurant.repository.RestaurantRepository;
import com.moyorak.api.team.domain.GeoPoint;
import com.moyorak.api.team.domain.TeamRestaurant;
import com.moyorak.api.team.domain.TeamRestaurantDistance;
import com.moyorak.api.team.domain.TeamRestaurantNotFoundException;
import com.moyorak.api.team.domain.TeamRestaurantSearch;
import com.moyorak.api.team.domain.TeamRestaurantSummaries;
import com.moyorak.api.team.domain.TeamUser;
import com.moyorak.api.team.dto.TeamRestaurantListRequest;
import com.moyorak.api.team.dto.TeamRestaurantLocation;
import com.moyorak.api.team.dto.TeamRestaurantLocationsResponse;
import com.moyorak.api.team.dto.TeamRestaurantResponse;
import com.moyorak.api.team.dto.TeamRestaurantSaveRequest;
import com.moyorak.api.team.dto.TeamRestaurantUpdateRequest;
import com.moyorak.api.team.repository.TeamRestaurantRepository;
import com.moyorak.api.team.repository.TeamRestaurantSearchRepository;
import com.moyorak.api.team.repository.TeamUserRepository;
import com.moyorak.config.exception.BusinessException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class TeamRestaurantService {
    private final TeamRestaurantRepository teamRestaurantRepository;
    private final TeamUserRepository teamUserRepository;
    private final RestaurantRepository restaurantRepository;
    private final TeamRestaurantSearchRepository teamRestaurantSearchRepository;
    private final TeamRestaurantEventPublisher teamRestaurantEventPublisher;

    @Transactional(readOnly = true)
    public TeamRestaurantResponse getTeamRestaurant(
            Long userId, Long teamId, Long teamRestaurantId) {
        final TeamRestaurant teamRestaurant = getValidatedTeamRestaurant(teamId, teamRestaurantId);

        // 조회 이벤트 발행
        teamRestaurantEventPublisher.publishViewEvent(userId, teamId, teamRestaurantId);
        return TeamRestaurantResponse.from(teamRestaurant);
    }

    @Transactional(readOnly = true)
    public Page<TeamRestaurant> getTeamRestaurants(
            Long teamId, TeamRestaurantListRequest teamRestaurantListRequest) {
        return teamRestaurantRepository.findAllByTeamId(
                teamId, teamRestaurantListRequest.toPageable());
    }

    @Transactional
    public void updateTeamRestaurant(
            Long teamId,
            Long teamRestaurantId,
            Long userId,
            TeamRestaurantUpdateRequest teamRestaurantUpdateRequest) {
        // 팀원인지 확인
        validateTeamUser(userId, teamId);
        final TeamRestaurant teamRestaurant = getValidatedTeamRestaurant(teamId, teamRestaurantId);
        teamRestaurant.updateSummary(teamRestaurantUpdateRequest.summary());
    }

    @Transactional
    public void deleteTeamRestaurant(Long teamId, Long teamRestaurantId, Long userId) {
        // 팀원인지 확인
        validateTeamUser(userId, teamId);
        final TeamRestaurant teamRestaurant = getValidatedTeamRestaurant(teamId, teamRestaurantId);
        // 사용 여부 값 변경
        teamRestaurant.toggleUse();

        final TeamRestaurantSearch teamRestaurantSearch =
                teamRestaurantSearchRepository
                        .findByTeamIdAndTeamRestaurantId(teamId, teamRestaurantId)
                        .orElseThrow(() -> new BusinessException("팀 맛집 검색 데이터가 없습니다."));

        // 사용 여부 값 변경
        teamRestaurantSearch.toggleUse();
    }

    @Transactional
    public void save(
            final Long userId,
            final Long teamId,
            final TeamRestaurantSaveRequest teamRestaurantSaveRequest) {

        // 팀원인지 확인
        final TeamUser teamUser = validateTeamUser(userId, teamId);

        // 식당 조회
        final Restaurant restaurant =
                restaurantRepository
                        .findByIdAndUse(teamRestaurantSaveRequest.restaurantId(), true)
                        .orElseThrow(() -> new BusinessException("식당이 존재하지 않습니다."));

        // 팀 맛집에 있는지 체크
        final boolean isPresent =
                teamRestaurantRepository
                        .findByTeamIdAndRestaurantIdAndUse(
                                teamId, teamRestaurantSaveRequest.restaurantId(), true)
                        .isPresent();
        if (isPresent) {
            throw new BusinessException("이미 등록된 팀 맛집입니다.");
        }

        // 거리 계산
        final TeamRestaurantDistance teamRestaurantDistance =
                createTeamPlaceDistance(teamUser, restaurant);
        final double distance = teamRestaurantDistance.calculateDistance();

        // 팀 맛집 디비와 서치용 디비에 저장
        final TeamRestaurant TeamRestaurant =
                teamRestaurantRepository.save(
                        teamRestaurantSaveRequest.toTeamRestaurant(teamId, restaurant, distance));
        teamRestaurantSearchRepository.save(TeamRestaurantSearch.from(TeamRestaurant, restaurant));
    }

    private TeamUser validateTeamUser(final Long userId, final Long teamId) {
        final TeamUser teamUser =
                teamUserRepository
                        .findWithTeamAndCompany(teamId, userId, true)
                        .orElseThrow(() -> new BusinessException("팀원이 아닙니다."));

        if (teamUser.isNotApproved()) {
            throw new BusinessException("팀원이 아닙니다.");
        }
        return teamUser;
    }

    private TeamRestaurantDistance createTeamPlaceDistance(
            final TeamUser teamUser, final Restaurant restaurant) {
        final GeoPoint companyPoint =
                GeoPoint.of(
                        teamUser.getTeam().getCompany().getLongitude(),
                        teamUser.getTeam().getCompany().getLatitude());

        final GeoPoint restaurantPoint =
                GeoPoint.of(restaurant.getLongitude(), restaurant.getLatitude());

        return TeamRestaurantDistance.of(companyPoint, restaurantPoint);
    }

    @Transactional(readOnly = true)
    public TeamRestaurantSummaries findByIdsAndUse(List<Long> ids, boolean use) {
        return TeamRestaurantSummaries.create(teamRestaurantRepository.findByIdInAndUse(ids, use));
    }

    @Transactional(readOnly = true)
    public TeamRestaurantLocationsResponse findTeamRestaurantLocations(final Long teamId) {
        final List<TeamRestaurantLocation> teamRestaurantLocations =
                teamRestaurantRepository.findLocationsByTeamIdAndUse(teamId, true);
        return TeamRestaurantLocationsResponse.of(teamRestaurantLocations);
    }

    @Transactional(readOnly = true)
    public TeamRestaurant getValidatedTeamRestaurant(Long teamId, Long teamRestaurantId) {
        final TeamRestaurant teamRestaurant =
                teamRestaurantRepository
                        .findByTeamIdAndIdAndUse(teamId, teamRestaurantId, true)
                        .orElseThrow(TeamRestaurantNotFoundException::new);
        if (teamRestaurant.isRestaurantNull()) {
            throw new BusinessException("연결된 식당 정보가 존재하지 않습니다.");
        }
        return teamRestaurant;
    }

    @Transactional
    public void updateAverageValue(
            final Long teamRestaurantId,
            final Integer reviewScore,
            final Integer servingTime,
            final Integer waitingTime) {
        teamRestaurantRepository.updateAverageValue(
                teamRestaurantId, reviewScore, servingTime, waitingTime);
    }
}
