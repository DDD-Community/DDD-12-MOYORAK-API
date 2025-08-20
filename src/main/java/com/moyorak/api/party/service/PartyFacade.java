package com.moyorak.api.party.service;

import com.moyorak.api.auth.dto.MealTagResponse;
import com.moyorak.api.auth.dto.UserDailyStatesResponse;
import com.moyorak.api.auth.service.MealTagService;
import com.moyorak.api.auth.service.UserService;
import com.moyorak.api.party.domain.Party;
import com.moyorak.api.party.dto.PartyAttendeeListResponse;
import com.moyorak.api.party.dto.PartyAttendeeWithUserProfile;
import com.moyorak.api.party.dto.PartyGeneralInfoProjection;
import com.moyorak.api.party.dto.PartyInfo;
import com.moyorak.api.party.dto.PartyListRequest;
import com.moyorak.api.party.dto.PartyListResponse;
import com.moyorak.api.party.dto.PartyListStore;
import com.moyorak.api.party.dto.PartyResponse;
import com.moyorak.api.party.dto.PartyRestaurantProjection;
import com.moyorak.api.party.dto.PartySaveRequest;
import com.moyorak.api.party.dto.RestaurantCandidateResponse;
import com.moyorak.api.party.dto.VoteDetail;
import com.moyorak.api.party.dto.Voter;
import com.moyorak.api.review.domain.FirstReviewPhotoPaths;
import com.moyorak.api.review.service.ReviewPhotoService;
import com.moyorak.api.team.domain.TeamRestaurantSummaries;
import com.moyorak.api.team.service.TeamRestaurantService;
import com.moyorak.api.team.service.TeamService;
import com.moyorak.config.exception.BusinessException;
import com.moyorak.global.domain.ListResponse;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PartyFacade {

    private final PartyService partyService;
    private final VoteService voteService;
    private final TeamRestaurantService teamRestaurantService;
    private final ReviewPhotoService reviewPhotoService;
    private final VoteRecordService voteRecordService;
    private final PartyRestaurantService partyRestaurantService;
    private final PartyAttendeeService partyAttendeeService;
    private final MealTagService mealTagService;
    private final TeamService teamService;
    private final UserService userService;

    @Transactional
    public PartyResponse getParty(final Long partyId, final Long teamId, final Long userId) {

        // 파티 정보 가져오기
        final PartyInfo partyInfo = partyService.getPartyInfo(partyId, teamId);

        // 투표 정보 가져오기
        final VoteDetail voteDetail = voteService.getVoteDetail(partyId, LocalDateTime.now());

        // 투표자 가져오기
        final List<Voter> voters = voteRecordService.getVoters(voteDetail.getCandidateIds());

        // 팀 식당 id로 필요한 정보 가져오기
        final TeamRestaurantSummaries teamRestaurantSummaries =
                teamRestaurantService.findByIdsAndUse(voteDetail.getTeamRestaurantIds(), true);

        // 팀 식당별로 리뷰 첫 사진 정보 가져오기
        final FirstReviewPhotoPaths firstReviewPhotoPaths =
                reviewPhotoService.findFirstReviewPhotoPaths(
                        teamRestaurantSummaries.getTeamRestaurantIds());

        // 참석 여부 확인
        final boolean attended = partyAttendeeService.existAttendee(partyId, userId);

        // 응답
        final List<RestaurantCandidateResponse> candidateResponses =
                RestaurantCandidateResponse.listFrom(
                        teamRestaurantSummaries,
                        firstReviewPhotoPaths,
                        voteDetail.RestaurantCandidates());
        return PartyResponse.from(
                partyInfo, voteDetail.voteInfo(), candidateResponses, voters, attended);
    }

    @Transactional
    public ListResponse<PartyListResponse> getParties(
            final Long teamId, final Long userId, final PartyListRequest partyListRequest) {
        final List<PartyGeneralInfoProjection> parties = partyService.findPartyGeneralInfos(teamId);
        final LocalDateTime now = LocalDateTime.now();
        parties.forEach(p -> voteService.updateVoteStatus(p.id(), now));
        final List<Long> partyIds = parties.stream().map(PartyGeneralInfoProjection::id).toList();

        final List<PartyRestaurantProjection> partyRestaurantProjections =
                partyRestaurantService.findPartyRestaurantInfo(partyIds);

        final List<PartyAttendeeWithUserProfile> partyAttendees =
                partyAttendeeService.findPartyAttendeeWithUserByPartyIds(partyIds);

        final PartyListStore partyListStore =
                PartyListStore.create(partyRestaurantProjections, partyAttendees, userId);
        final List<PartyListResponse> partyListResponses =
                PartyListResponse.from(parties, partyListStore);

        return ListResponse.from(
                PartyListResponse.toPage(partyListResponses, partyListRequest.toPageable()));
    }

    /**
     * 파티를 생성합니다.
     *
     * @param teamId 팀 고유 ID
     * @param request 파티 생성 요청 DTO
     */
    @Transactional
    public void partyRegister(final Long teamId, final PartySaveRequest request) {
        // 1. 팀 존재 여부 확인
        if (!teamService.existTeam(teamId)) {
            throw new BusinessException("존재하지 않는 팀입니다.");
        }

        // 2. 파티 생성
        final Long partyId =
                partyService.register(teamId, request.getTitle(), request.getContent());

        // 3. 투표 생성
        final Long voteId = voteService.register(partyId, request);

        // 4. 파티 회원 등록
        partyAttendeeService.registerUsers(partyId, request.getUserSelections().getUsers());

        // 5. 파티 식당 등록
        partyRestaurantService.registerRestaurants(
                voteId, request.getRestaurantSelections().getRestaurants());
    }

    @Transactional(readOnly = true)
    public List<PartyAttendeeListResponse> getPartyAttendees(
            final Long partyId, final Long teamId) {
        final Party party = partyService.getParty(partyId);
        if (!party.getTeamId().equals(teamId)) {
            throw new BusinessException("해당 팀의 파티가 아닙니다.");
        }
        final List<PartyAttendeeWithUserProfile> partyAttendees =
                partyAttendeeService.findPartyAttendeeWithUserByPartyIds(List.of(partyId));

        final List<Long> userIds =
                partyAttendees.stream().map(PartyAttendeeWithUserProfile::userId).toList();
        final Map<Long, MealTagResponse> mealTagMap = mealTagService.getMealTags(userIds);

        return PartyAttendeeListResponse.fromList(partyAttendees, mealTagMap);
    }

    @Transactional(readOnly = true)
    public List<UserDailyStatesResponse> getUsers(final Long teamId, final Long userId) {
        // 1. 팀 존재 여부 확인
        if (!teamService.existTeam(teamId)) {
            throw new BusinessException("존재하지 않는 팀입니다.");
        }

        // 2. 혼밥 정보를 포함한 회원 정보 조회
        return userService.getUsersWithDailyState(LocalDate.now(), userId);
    }
}
