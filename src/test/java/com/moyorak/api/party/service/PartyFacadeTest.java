package com.moyorak.api.party.service;

import static org.assertj.core.api.SoftAssertions.assertSoftly;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

import com.moyorak.api.party.domain.PartyAttendee;
import com.moyorak.api.party.domain.PartyAttendeeFixture;
import com.moyorak.api.party.domain.PartyGeneralInfoProjectionFixture;
import com.moyorak.api.party.domain.PartyRestaurantProjectionFixture;
import com.moyorak.api.party.domain.VoteStatus;
import com.moyorak.api.party.domain.VoteType;
import com.moyorak.api.party.dto.PartyGeneralInfoProjection;
import com.moyorak.api.party.dto.PartyListRequest;
import com.moyorak.api.party.dto.PartyListRequestFixture;
import com.moyorak.api.party.dto.PartyListResponse;
import com.moyorak.api.party.dto.PartyRestaurantProjection;
import com.moyorak.api.restaurant.domain.RestaurantCategory;
import com.moyorak.global.domain.ListResponse;
import java.time.LocalDateTime;
import java.util.List;
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

    @Nested
    @DisplayName("파티 목록 조회 시")
    class GetParties {

        private final Long teamId = 1L;
        private final Long userId = 9L;

        private final PartyListRequest request = PartyListRequestFixture.fixture(5, 1);

        @Test
        @DisplayName("파티가 존재하면 정상적으로 응답한다")
        void success() {
            // given
            final Long partyId = 1L;
            final Long partyAttendeeId = 1L;

            final PartyGeneralInfoProjection generalInfo =
                    PartyGeneralInfoProjectionFixture.fixture(
                            partyId,
                            LocalDateTime.now(),
                            "점심팟 구합니다.",
                            VoteType.SELECT,
                            VoteStatus.VOTING,
                            3L // attendeeCount (프로젝트에서 int/Long 확인)
                            );
            final List<PartyGeneralInfoProjection> parties = List.of(generalInfo);
            given(partyService.findPartyGeneralInfos(teamId)).willReturn(parties);

            final List<Long> partyIds = List.of(partyId);

            final PartyRestaurantProjection restaurantProjection =
                    PartyRestaurantProjectionFixture.fixture(
                            partyId, "음식점", RestaurantCategory.KOREAN, 5.0, 5);
            given(partyRestaurantService.findPartyRestaurantInfo(partyIds))
                    .willReturn(List.of(restaurantProjection));

            final PartyAttendee attendee =
                    PartyAttendeeFixture.fixture(partyAttendeeId, true, partyId, userId);
            given(partyAttendeeService.findByPartyIds(partyIds)).willReturn(List.of(attendee));

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

            verify(partyService).findPartyGeneralInfos(teamId);
            verify(partyRestaurantService).findPartyRestaurantInfo(partyIds);
            verify(partyAttendeeService).findByPartyIds(partyIds);
        }

        @Test
        @DisplayName("파티가 없으면 빈 결과를 반환한다")
        void emptyParties() {
            // given
            given(partyService.findPartyGeneralInfos(teamId)).willReturn(List.of());

            given(partyRestaurantService.findPartyRestaurantInfo(List.of())).willReturn(List.of());
            given(partyAttendeeService.findByPartyIds(List.of())).willReturn(List.of());

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
            verify(partyAttendeeService).findByPartyIds(List.of());
        }
    }
}
