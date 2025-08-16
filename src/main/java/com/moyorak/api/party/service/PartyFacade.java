package com.moyorak.api.party.service;

import com.moyorak.api.party.dto.PartyAttendeeWithUserProfile;
import com.moyorak.api.party.dto.PartyGeneralInfoProjection;
import com.moyorak.api.party.dto.PartyInfo;
import com.moyorak.api.party.dto.PartyListRequest;
import com.moyorak.api.party.dto.PartyListResponse;
import com.moyorak.api.party.dto.PartyListStore;
import com.moyorak.api.party.dto.PartyResponse;
import com.moyorak.api.party.dto.PartyRestaurantProjection;
import com.moyorak.api.party.dto.RestaurantCandidateResponse;
import com.moyorak.api.party.dto.VoteDetail;
import com.moyorak.api.party.dto.Voter;
import com.moyorak.api.review.domain.FirstReviewPhotoPaths;
import com.moyorak.api.review.service.ReviewPhotoService;
import com.moyorak.api.team.domain.TeamRestaurantSummaries;
import com.moyorak.api.team.service.TeamRestaurantService;
import com.moyorak.global.domain.ListResponse;
import java.time.LocalDateTime;
import java.util.List;
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

    @Transactional
    public PartyResponse getParty(final Long partyId) {

        // 파티 정보 가져오기
        final PartyInfo partyInfo = partyService.getPartyInfo(partyId);

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

        // 응답
        final List<RestaurantCandidateResponse> candidateResponses =
                RestaurantCandidateResponse.listFrom(
                        teamRestaurantSummaries,
                        firstReviewPhotoPaths,
                        voteDetail.RestaurantCandidates());
        return PartyResponse.from(partyInfo, voteDetail.voteInfo(), candidateResponses, voters);
    }

    @Transactional(readOnly = true)
    public ListResponse<PartyListResponse> getParties(
            final Long teamId, final Long userId, final PartyListRequest partyListRequest) {
        final List<PartyGeneralInfoProjection> parties = partyService.findPartyGeneralInfos(teamId);

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
}
