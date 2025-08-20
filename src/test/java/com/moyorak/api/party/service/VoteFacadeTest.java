package com.moyorak.api.party.service;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willThrow;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

import com.moyorak.api.party.domain.PartyAttendee;
import com.moyorak.api.party.domain.PartyAttendeeFixture;
import com.moyorak.api.party.domain.PartyInfoFixture;
import com.moyorak.api.party.domain.VoteRestaurantCandidate;
import com.moyorak.api.party.domain.VoteRestaurantCandidateFixture;
import com.moyorak.api.party.dto.PartyInfo;
import com.moyorak.api.party.dto.VoteRequest;
import com.moyorak.api.party.dto.VoteRequestFixture;
import com.moyorak.config.exception.BusinessException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class VoteFacadeTest {

    @InjectMocks private VoteFacade voteFacade;

    @Mock private PartyService partyService;
    @Mock private VoteService voteService;
    @Mock private PartyAttendeeService partyAttendeeService;
    @Mock private PartyRestaurantService partyRestaurantService;
    @Mock private VoteRecordService voteRecordService;

    private final Long teamId = 1L;
    private final Long partyId = 10L;
    private final Long voteId = 100L;
    private final Long userId = 77L;

    private VoteRequest voteRequest(final Long candidateId) {
        return VoteRequestFixture.fixture(candidateId);
    }

    private PartyInfo partyInfo() {
        return PartyInfoFixture.fixture(partyId, "제목", "내용", true);
    }

    @Nested
    @DisplayName("투표시 ")
    class Vote {

        @Test
        @DisplayName("파티의 팀과 요청 받은 팀이 다르면 예외를 발생시킨다.")
        void teamMismatch() {
            // given
            willThrow(new BusinessException("팀에 해당 파티가 존재하지 않습니다."))
                    .given(partyService)
                    .getPartyInfo(partyId, teamId);

            // when & then
            assertThatThrownBy(
                            () -> voteFacade.vote(teamId, partyId, voteId, userId, voteRequest(9L)))
                    .hasMessage("팀에 해당 파티가 존재하지 않습니다.");
        }

        @Test
        @DisplayName("파티 참가자가 아니면 예외를 발생시킨다.")
        void notPartyAttendee() {
            // given
            given(partyService.getPartyInfo(partyId, teamId)).willReturn(partyInfo());

            doThrow(new BusinessException("파티 참가자가 존재하지 않습니다."))
                    .when(partyAttendeeService)
                    .getPartyAttendeeByUserIdAndPartyId(userId, partyId);

            // when & then
            assertThatThrownBy(
                            () -> voteFacade.vote(teamId, partyId, voteId, userId, voteRequest(9L)))
                    .isInstanceOf(BusinessException.class)
                    .hasMessage("파티 참가자가 존재하지 않습니다.");
        }

        @Test
        @DisplayName("투표가 존재하지 않으면 예외를 발생시킨다.")
        void voteNotFound() {
            // given
            final PartyAttendee partyAttendee =
                    PartyAttendeeFixture.fixture(1L, true, partyId, userId);
            given(partyService.getPartyInfo(partyId, teamId)).willReturn(partyInfo());
            given(partyAttendeeService.getPartyAttendeeByUserIdAndPartyId(userId, partyId))
                    .willReturn(partyAttendee);

            willThrow(new BusinessException("투표가 존재하지 않습니다."))
                    .given(voteService)
                    .getVoteByIdAndPartyId(voteId, partyId);

            // when & then
            assertThatThrownBy(
                            () -> voteFacade.vote(teamId, partyId, voteId, userId, voteRequest(9L)))
                    .isInstanceOf(BusinessException.class)
                    .hasMessage("투표가 존재하지 않습니다.");
        }

        @Test
        @DisplayName("후보가 존재하지 않으면 예외를 발생시킨다.")
        void candidateNotFound() {
            // given

            final PartyAttendee partyAttendee =
                    PartyAttendeeFixture.fixture(1L, true, partyId, userId);

            given(partyService.getPartyInfo(partyId, teamId)).willReturn(partyInfo());
            given(partyAttendeeService.getPartyAttendeeByUserIdAndPartyId(userId, partyId))
                    .willReturn(partyAttendee);

            willThrow(new BusinessException("후보가 존재하지 않습니다."))
                    .given(partyRestaurantService)
                    .getById(9L);

            // when & then
            assertThatThrownBy(
                            () -> voteFacade.vote(teamId, partyId, voteId, userId, voteRequest(9L)))
                    .isInstanceOf(BusinessException.class)
                    .hasMessage("후보가 존재하지 않습니다.");
        }

        @Test
        @DisplayName("후보가 해당 투표에 속하지 않으면 예외를 발생시킨다.")
        void candidateNotInVote() {
            // given
            final Long candidateId = 9L;
            final PartyAttendee partyAttendee =
                    PartyAttendeeFixture.fixture(1L, true, partyId, userId);

            given(partyService.getPartyInfo(partyId, teamId)).willReturn(partyInfo());
            given(partyAttendeeService.getPartyAttendeeByUserIdAndPartyId(userId, partyId))
                    .willReturn(partyAttendee);

            final VoteRestaurantCandidate candidate =
                    VoteRestaurantCandidateFixture.fixture(candidateId, 999L, 5L, true);
            given(partyRestaurantService.getById(candidateId)).willReturn(candidate);

            // when & then
            assertThatThrownBy(
                            () ->
                                    voteFacade.vote(
                                            teamId,
                                            partyId,
                                            voteId,
                                            userId,
                                            voteRequest(candidateId)))
                    .isInstanceOf(BusinessException.class)
                    .hasMessage("해당 투표의 후보가 아닙니다.");
        }

        @Test
        @DisplayName("이미 동일 후보에 투표한 경우 예외를 발생시킨다.")
        void alreadyVotedSameCandidate() {
            // given
            final Long candidateId = 9L;
            final PartyAttendee partyAttendee =
                    PartyAttendeeFixture.fixture(1L, true, partyId, userId);

            given(partyService.getPartyInfo(partyId, teamId)).willReturn(partyInfo());
            given(partyAttendeeService.getPartyAttendeeByUserIdAndPartyId(userId, partyId))
                    .willReturn(partyAttendee);

            final VoteRestaurantCandidate candidate =
                    VoteRestaurantCandidateFixture.fixture(candidateId, voteId, 5L, true);
            given(partyRestaurantService.getById(candidateId)).willReturn(candidate);

            willThrow(new BusinessException("이미 투표한 후보 입니다."))
                    .given(voteRecordService)
                    .vote(userId, voteId, candidateId);

            // when & then
            assertThatThrownBy(
                            () ->
                                    voteFacade.vote(
                                            teamId,
                                            partyId,
                                            voteId,
                                            userId,
                                            voteRequest(candidateId)))
                    .isInstanceOf(BusinessException.class)
                    .hasMessage("이미 투표한 후보 입니다.");
        }

        @Test
        @DisplayName("최초 투표 시 기존 레코드가 없고 새 레코드를 저장한다.")
        void firstVote() {
            // given

            final Long candidateId = 3333L;
            final PartyAttendee partyAttendee =
                    PartyAttendeeFixture.fixture(1L, true, partyId, userId);

            given(partyService.getPartyInfo(partyId, teamId)).willReturn(partyInfo());
            given(partyAttendeeService.getPartyAttendeeByUserIdAndPartyId(userId, partyId))
                    .willReturn(partyAttendee);

            final VoteRestaurantCandidate candidate =
                    VoteRestaurantCandidateFixture.fixture(candidateId, voteId, 444L, true);
            given(partyRestaurantService.getById(candidateId)).willReturn(candidate);

            // when
            voteFacade.vote(teamId, partyId, voteId, userId, voteRequest(candidateId));

            // then
            verify(voteRecordService, times(1)).vote(userId, voteId, candidateId);
            verifyNoMoreInteractions(voteRecordService);
        }
    }
}
