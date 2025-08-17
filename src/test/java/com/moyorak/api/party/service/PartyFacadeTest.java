package com.moyorak.api.party.service;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.assertj.core.api.SoftAssertions.assertSoftly;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

import com.moyorak.api.auth.dto.MealTagResponseFixture;
import com.moyorak.api.auth.service.MealTagService;
import com.moyorak.api.party.domain.Party;
import com.moyorak.api.party.domain.PartyAttendeeWithUserFixture;
import com.moyorak.api.party.domain.PartyFixture;
import com.moyorak.api.party.domain.PartyGeneralInfoProjectionFixture;
import com.moyorak.api.party.domain.PartyRestaurantProjectionFixture;
import com.moyorak.api.party.domain.VoteStatus;
import com.moyorak.api.party.domain.VoteType;
import com.moyorak.api.party.dto.PartyAttendeeListResponse;
import com.moyorak.api.party.dto.PartyAttendeeWithUserProfile;
import com.moyorak.api.party.dto.PartyGeneralInfoProjection;
import com.moyorak.api.party.dto.PartyListRequest;
import com.moyorak.api.party.dto.PartyListRequestFixture;
import com.moyorak.api.party.dto.PartyListResponse;
import com.moyorak.api.party.dto.PartyRestaurantProjection;
import com.moyorak.api.restaurant.domain.RestaurantCategory;
import com.moyorak.config.exception.BusinessException;
import com.moyorak.global.domain.ListResponse;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class PartyFacadeTest {

    @InjectMocks private PartyFacade partyFacade;

    @Mock private PartyService partyService;
    @Mock private PartyAttendeeService partyAttendeeService;
    @Mock private PartyRestaurantService partyRestaurantService;
    @Mock private VoteService voteService;
    @Mock private MealTagService mealTagService;

    @Nested
    @DisplayName("파티 목록 조회 시")
    class GetParties {

        private final Long teamId = 1L;
        private final Long userId = 9L;
        private final String userName = "홍길동";

        private final PartyListRequest request = PartyListRequestFixture.fixture(5, 1);

        @Test
        @DisplayName("파티가 존재하면 정상적으로 응답한다")
        void success() {
            // given
            final Long partyId = 1L;

            final PartyGeneralInfoProjection generalInfo =
                    PartyGeneralInfoProjectionFixture.fixture(
                            partyId,
                            LocalDateTime.now(),
                            LocalDateTime.now(),
                            "점심팟 구합니다.",
                            VoteType.SELECT,
                            VoteStatus.VOTING,
                            3L);
            final List<PartyGeneralInfoProjection> parties = List.of(generalInfo);
            given(partyService.findPartyGeneralInfos(teamId)).willReturn(parties);

            final List<Long> partyIds = List.of(partyId);

            final PartyRestaurantProjection restaurantProjection =
                    PartyRestaurantProjectionFixture.fixture(
                            partyId, "음식점", RestaurantCategory.KOREAN, 5.0, 5);
            given(partyRestaurantService.findPartyRestaurantInfo(partyIds))
                    .willReturn(List.of(restaurantProjection));

            final PartyAttendeeWithUserProfile attendee =
                    PartyAttendeeWithUserFixture.fixture(
                            partyId, userId, userName, "http://test.path");
            given(partyAttendeeService.findPartyAttendeeWithUserByPartyIds(partyIds))
                    .willReturn(List.of(attendee));

            // when
            final ListResponse<PartyListResponse> result =
                    partyFacade.getParties(teamId, userId, request);

            // then
            assertSoftly(
                    it -> {
                        it.assertThat(result).isNotNull();
                        it.assertThat(result.getData().getFirst().id()).isEqualTo(partyId);
                        it.assertThat(
                                        result.getData()
                                                .getFirst()
                                                .partyRestaurantResponseList()
                                                .getFirst()
                                                .name())
                                .isEqualTo("음식점");
                        it.assertThat(result.getData()).hasSize(1);
                    });

            verify(voteService).updateVoteStatus(eq(partyId), any(LocalDateTime.class));
            verify(partyService).findPartyGeneralInfos(teamId);
            verify(partyRestaurantService).findPartyRestaurantInfo(partyIds);
            verify(partyAttendeeService).findPartyAttendeeWithUserByPartyIds(partyIds);
        }

        @Test
        @DisplayName("파티가 없으면 빈 결과를 반환한다")
        void emptyParties() {
            // given
            given(partyService.findPartyGeneralInfos(teamId)).willReturn(List.of());

            given(partyRestaurantService.findPartyRestaurantInfo(List.of())).willReturn(List.of());
            given(partyAttendeeService.findPartyAttendeeWithUserByPartyIds(List.of()))
                    .willReturn(List.of());

            // when
            final ListResponse<PartyListResponse> result =
                    partyFacade.getParties(teamId, userId, request);

            // then
            assertSoftly(
                    it -> {
                        it.assertThat(result).isNotNull();
                        it.assertThat(result.getData()).isNotNull();
                        it.assertThat(result.getData()).isEmpty();
                    });
            // 상호작용 검증
            verify(partyService).findPartyGeneralInfos(teamId);
            verify(partyRestaurantService).findPartyRestaurantInfo(List.of());
            verify(partyAttendeeService).findPartyAttendeeWithUserByPartyIds(List.of());
        }
    }

    @Nested
    @DisplayName("파티 참가자 조회 시")
    class GetPartyAttendees {

        private final Long partyId = 10L;
        private final Long teamId = 1L;
        private final Long userId = 9L;
        private final String userName = "홍길동";

        @Test
        @DisplayName("참가자가 존재하면 정상적으로 응답한다")
        void successWhenAttendeesExist() {
            // given
            final Party party = PartyFixture.fixture(partyId, teamId);
            given(partyService.getParty(partyId)).willReturn(party);

            final PartyAttendeeWithUserProfile attendee =
                    PartyAttendeeWithUserFixture.fixture(
                            partyId, userId, userName, "http://test.path");

            given(partyAttendeeService.findPartyAttendeeWithUserByPartyIds(List.of(partyId)))
                    .willReturn(List.of(attendee));

            given(mealTagService.getMealTags(List.of(userId)))
                    .willReturn(Map.of(userId, MealTagResponseFixture.fixture("해삼", "복숭아")));

            // when
            final List<PartyAttendeeListResponse> result =
                    partyFacade.getPartyAttendees(partyId, teamId);

            // then
            assertSoftly(
                    it -> {
                        it.assertThat(result).isNotNull();
                        it.assertThat(result).hasSize(1);
                    });

            verify(partyService).getParty(partyId);
            verify(partyAttendeeService).findPartyAttendeeWithUserByPartyIds(List.of(partyId));
            verify(mealTagService).getMealTags(List.of(userId));
            verifyNoMoreInteractions(partyService, partyAttendeeService, mealTagService);
        }

        @Test
        @DisplayName("해당 팀의 파티가 아니면 예외를 던진다")
        void failWhenTeamMismatch() {
            // given
            final Party party = PartyFixture.fixture(partyId, 999L);
            given(partyService.getParty(partyId)).willReturn(party);

            // when & then
            assertThatThrownBy(() -> partyFacade.getPartyAttendees(partyId, teamId))
                    .isInstanceOf(BusinessException.class)
                    .hasMessageContaining("해당 팀의 파티가 아닙니다.");

            verify(partyService).getParty(partyId);
            verifyNoInteractions(partyAttendeeService);
            verifyNoInteractions(mealTagService);
        }

        @Test
        @DisplayName("참가자가 없으면 빈 리스트로 응답한다")
        void successWhenNoAttendees() {
            // given
            final Party party = PartyFixture.fixture(partyId, teamId);
            given(partyService.getParty(partyId)).willReturn(party);

            given(partyAttendeeService.findPartyAttendeeWithUserByPartyIds(List.of(partyId)))
                    .willReturn(Collections.emptyList());

            given(mealTagService.getMealTags(Collections.emptyList()))
                    .willReturn(Collections.emptyMap());

            // when
            final List<PartyAttendeeListResponse> result =
                    partyFacade.getPartyAttendees(partyId, teamId);

            // then
            assertSoftly(
                    it -> {
                        it.assertThat(result).isNotNull();
                        it.assertThat(result).isEmpty();
                    });

            verify(partyService).getParty(partyId);
            verify(partyAttendeeService).findPartyAttendeeWithUserByPartyIds(List.of(partyId));
            verify(mealTagService).getMealTags(Collections.emptyList());
            verifyNoMoreInteractions(partyService, partyAttendeeService, mealTagService);
        }
    }
}
