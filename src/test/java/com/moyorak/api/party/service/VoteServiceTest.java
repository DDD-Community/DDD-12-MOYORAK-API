package com.moyorak.api.party.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

import com.moyorak.api.party.domain.RandomVoteInfo;
import com.moyorak.api.party.domain.RandomVoteInfoFixture;
import com.moyorak.api.party.domain.SelectionVoteInfo;
import com.moyorak.api.party.domain.SelectionVoteInfoFixture;
import com.moyorak.api.party.domain.Vote;
import com.moyorak.api.party.domain.VoteFixture;
import com.moyorak.api.party.domain.VoteRestaurantCandidate;
import com.moyorak.api.party.domain.VoteRestaurantCandidateFixture;
import com.moyorak.api.party.domain.VoteStatus;
import com.moyorak.api.party.domain.VoteType;
import com.moyorak.api.party.dto.VoteDetail;
import com.moyorak.api.party.repository.RandomVoteInfoRepository;
import com.moyorak.api.party.repository.SelectionVoteInfoRepository;
import com.moyorak.api.party.repository.VoteRepository;
import com.moyorak.api.party.repository.VoteRestaurantCandidateRepository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class VoteServiceTest {

    @InjectMocks private VoteService voteService;

    @Mock private VoteRepository voteRepository;

    @Mock private VoteRestaurantCandidateRepository candidateRepository;

    @Mock private SelectionVoteInfoRepository selectionVoteInfoRepository;

    @Mock private RandomVoteInfoRepository randomVoteInfoRepository;

    @Nested
    @DisplayName("getVoteDetail은")
    class GetVoteDetail {

        @Nested
        @DisplayName("선택투표인 경우")
        class Selection {

            private final Long partyId = 1L;
            private final Long voteId = 1L;
            private final LocalDateTime now = LocalDateTime.of(2025, 8, 14, 12, 0, 0);

            @Test
            @DisplayName("현재 시간이 투표 시작 시간과 만료 시간 사이면 투표 상태를 VOTIN으로 변경한다.")
            void isVoting() {
                // given
                final Vote vote =
                        VoteFixture.fixture(
                                voteId, VoteType.SELECT, VoteStatus.READY, true, partyId);

                final LocalDateTime startDate = now.minusMinutes(10);
                final LocalDateTime mealDate = now.plusMinutes(30);
                final LocalDateTime expiredDate = now.plusMinutes(10);
                final SelectionVoteInfo selectionVoteInfo =
                        SelectionVoteInfoFixture.fixture(mealDate, startDate, expiredDate);

                final VoteRestaurantCandidate c1 =
                        VoteRestaurantCandidateFixture.fixture(1L, voteId, 11L, true);
                final VoteRestaurantCandidate c2 =
                        VoteRestaurantCandidateFixture.fixture(2L, voteId, 12L, true);
                final List<VoteRestaurantCandidate> candidates = List.of(c1, c2);

                given(voteRepository.findByPartyIdAndUseTrue(partyId))
                        .willReturn(Optional.of(vote));
                given(candidateRepository.findAllByVoteIdAndUseTrue(voteId)).willReturn(candidates);
                given(selectionVoteInfoRepository.findByVoteIdAndUseTrue(voteId))
                        .willReturn(Optional.of(selectionVoteInfo));

                // when
                final VoteDetail result = voteService.getVoteDetail(partyId, now);

                // then
                assertThat(result).isNotNull();
                assertThat(result.voteInfo()).isNotNull();
                assertThat(result.voteInfo().id()).isEqualTo(voteId);
                assertThat(result.voteInfo().voteType()).isEqualTo(VoteType.SELECT);
                assertThat(result.voteInfo().voteStatus()).isEqualTo(VoteStatus.VOTING);
                assertThat(result.voteInfo().mealDate()).isEqualTo(selectionVoteInfo.getMealDate());
                assertThat(result.voteInfo().startDate()).isEqualTo(startDate);
                assertThat(result.voteInfo().expiredDate()).isEqualTo(expiredDate);
            }

            @Test
            @DisplayName("현재 시간이 만료 시간 이후면 투표 상태를 DONE으로 변경한다.")
            void isDone() {
                // given
                final Vote vote =
                        VoteFixture.fixture(
                                voteId, VoteType.SELECT, VoteStatus.READY, true, partyId);

                final LocalDateTime startDate = now.minusMinutes(10);
                final LocalDateTime mealDate = now.plusMinutes(30);
                final LocalDateTime expiredDate = now.minusMinutes(5);
                final SelectionVoteInfo selectionVoteInfo =
                        SelectionVoteInfoFixture.fixture(mealDate, startDate, expiredDate);

                final VoteRestaurantCandidate c1 =
                        VoteRestaurantCandidateFixture.fixture(1L, voteId, 11L, true);
                final VoteRestaurantCandidate c2 =
                        VoteRestaurantCandidateFixture.fixture(2L, voteId, 12L, true);
                final List<VoteRestaurantCandidate> candidates = List.of(c1, c2);

                given(voteRepository.findByPartyIdAndUseTrue(partyId))
                        .willReturn(Optional.of(vote));
                given(candidateRepository.findAllByVoteIdAndUseTrue(voteId)).willReturn(candidates);
                given(selectionVoteInfoRepository.findByVoteIdAndUseTrue(voteId))
                        .willReturn(Optional.of(selectionVoteInfo));

                // when
                final VoteDetail result = voteService.getVoteDetail(partyId, now);

                // then
                assertThat(result).isNotNull();
                assertThat(result.voteInfo()).isNotNull();
                assertThat(result.voteInfo().id()).isEqualTo(voteId);
                assertThat(result.voteInfo().voteType()).isEqualTo(VoteType.SELECT);
                assertThat(result.voteInfo().voteStatus()).isEqualTo(VoteStatus.DONE);
                assertThat(result.voteInfo().mealDate()).isEqualTo(selectionVoteInfo.getMealDate());
                assertThat(result.voteInfo().startDate()).isEqualTo(startDate);
                assertThat(result.voteInfo().expiredDate()).isEqualTo(expiredDate);
            }
        }

        @Nested
        @DisplayName("랜덤 투표인 경우")
        class Random {
            private final Long partyId = 1L;
            private final Long voteId = 1L;
            private final LocalDateTime now = LocalDateTime.of(2025, 8, 14, 12, 0, 0);

            @Test
            @DisplayName("현재 시간이 랜덤 추첨 시간 이후면 투표 상태를 DONE으로 변경하고 후보 식당에서 랜덤 선택한다.(update=1)")
            void isDoneAndUpdate() {
                // given
                final Vote vote =
                        VoteFixture.fixture(
                                voteId, VoteType.RANDOM, VoteStatus.READY, true, partyId);

                final LocalDateTime mealDate = now.plusMinutes(30);
                final LocalDateTime randomDate = now.minusMinutes(10);

                final RandomVoteInfo randomVoteInfo =
                        RandomVoteInfoFixture.fixture(randomDate, mealDate, voteId, true, null);

                final VoteRestaurantCandidate c1 =
                        VoteRestaurantCandidateFixture.fixture(123L, voteId, 11L, true);
                final VoteRestaurantCandidate c2 =
                        VoteRestaurantCandidateFixture.fixture(122L, voteId, 12L, true);
                final List<VoteRestaurantCandidate> candidates = List.of(c1, c2);

                final Long randomId = c2.getId();

                given(voteRepository.findByPartyIdAndUseTrue(voteId)).willReturn(Optional.of(vote));
                given(candidateRepository.findAllByVoteIdAndUseTrue(voteId)).willReturn(candidates);
                given(randomVoteInfoRepository.findByVoteIdAndUseTrue(voteId))
                        .willReturn(Optional.of(randomVoteInfo));
                given(
                                randomVoteInfoRepository.updateSelectedCandidate(
                                        randomVoteInfo.getId(), randomId))
                        .willReturn(1);

                // when
                VoteDetail result = voteService.getVoteDetail(partyId, now);

                // then
                assertThat(result.voteInfo().voteType()).isEqualTo(VoteType.RANDOM);
                assertThat(result.voteInfo().voteStatus()).isEqualTo(VoteStatus.DONE);
                assertThat(result.voteInfo().randomDate()).isEqualTo(randomDate);
                assertThat(result.voteInfo().selectedCandidateId()).isEqualTo(randomId);
            }

            @Test
            @DisplayName(
                    "현재 시간이 랜덤 추첨 시간 이후면 투표 상태를 DONE으로 변경하고 후보 식당에서 랜덤 선택한다.(update=0) 일시 최신 커밋을 조회하여 업데이트한다.")
            void isDoneAndFindLatestSelectedCandidateId() {
                // given
                final Vote vote =
                        VoteFixture.fixture(
                                voteId, VoteType.RANDOM, VoteStatus.READY, true, partyId);

                final LocalDateTime mealDate = now.plusMinutes(30);
                final LocalDateTime randomDate = now.minusMinutes(10);

                final RandomVoteInfo randomVoteInfo =
                        RandomVoteInfoFixture.fixture(randomDate, mealDate, voteId, true, null);

                final VoteRestaurantCandidate c1 =
                        VoteRestaurantCandidateFixture.fixture(123L, voteId, 11L, true);
                final VoteRestaurantCandidate c2 =
                        VoteRestaurantCandidateFixture.fixture(122L, voteId, 12L, true);
                final List<VoteRestaurantCandidate> candidates = List.of(c1, c2);

                final Long randomId = c2.getId();

                given(voteRepository.findByPartyIdAndUseTrue(voteId)).willReturn(Optional.of(vote));
                given(candidateRepository.findAllByVoteIdAndUseTrue(voteId)).willReturn(candidates);
                given(randomVoteInfoRepository.findByVoteIdAndUseTrue(voteId))
                        .willReturn(Optional.of(randomVoteInfo));
                given(
                                randomVoteInfoRepository.updateSelectedCandidate(
                                        randomVoteInfo.getId(), randomId))
                        .willReturn(0);
                given(randomVoteInfoRepository.findSelectedCandidateIdById(randomVoteInfo.getId()))
                        .willReturn(randomId);

                // when
                VoteDetail result = voteService.getVoteDetail(partyId, now);

                // then
                assertThat(result.voteInfo().voteType()).isEqualTo(VoteType.RANDOM);
                assertThat(result.voteInfo().voteStatus()).isEqualTo(VoteStatus.DONE);
                assertThat(result.voteInfo().randomDate()).isEqualTo(randomDate);
                assertThat(result.voteInfo().selectedCandidateId()).isEqualTo(randomId);
            }
        }
    }
}
