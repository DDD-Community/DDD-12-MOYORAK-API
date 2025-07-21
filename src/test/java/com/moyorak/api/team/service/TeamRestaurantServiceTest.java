package com.moyorak.api.team.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.mock;

import com.moyorak.api.company.domain.Company;
import com.moyorak.api.company.domain.CompanyFixture;
import com.moyorak.api.restaurant.domain.Restaurant;
import com.moyorak.api.restaurant.domain.RestaurantCategory;
import com.moyorak.api.restaurant.domain.RestaurantFixture;
import com.moyorak.api.restaurant.repository.RestaurantRepository;
import com.moyorak.api.team.domain.Team;
import com.moyorak.api.team.domain.TeamFixture;
import com.moyorak.api.team.domain.TeamRestaurant;
import com.moyorak.api.team.domain.TeamRestaurantFixture;
import com.moyorak.api.team.domain.TeamRestaurantNotFoundException;
import com.moyorak.api.team.domain.TeamRestaurantSearch;
import com.moyorak.api.team.domain.TeamUser;
import com.moyorak.api.team.domain.TeamUserFixture;
import com.moyorak.api.team.domain.TeamUserStatus;
import com.moyorak.api.team.dto.TeamRestaurantResponse;
import com.moyorak.api.team.dto.TeamRestaurantSaveRequest;
import com.moyorak.api.team.dto.TeamRestaurantUpdateRequest;
import com.moyorak.api.team.repository.TeamRestaurantRepository;
import com.moyorak.api.team.repository.TeamRestaurantSearchRepository;
import com.moyorak.api.team.repository.TeamUserRepository;
import com.moyorak.config.exception.BusinessException;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class TeamRestaurantServiceTest {

    @InjectMocks private TeamRestaurantService teamRestaurantService;

    @Mock private TeamRestaurantRepository teamRestaurantRepository;
    @Mock private TeamUserRepository teamUserRepository;
    @Mock private RestaurantRepository restaurantRepository;
    @Mock private TeamRestaurantSearchRepository teamRestaurantSearchRepository;

    final Long teamId = 1L;
    final Long teamRestaurantId = 1L;

    @Nested
    @DisplayName("팀 맛집 상세 조회 시,")
    class GetTeamRestaurant {

        @Test
        @DisplayName("정상적으로 조회되면 응답 DTO를 반환합니다.")
        void returnTeamRestaurantResponse() {
            // given
            final Restaurant restaurant =
                    RestaurantFixture.fixture(
                            "http://place.map.kakao.com/123456",
                            "우가우가 차차차",
                            "우가우가시 차차차동 24번길",
                            "우가우가 차차로 123",
                            RestaurantCategory.KOREAN,
                            127.043616,
                            37.503095);
            final TeamRestaurant teamRestaurant =
                    TeamRestaurantFixture.fixture(
                            teamRestaurantId, "맛있네요", 4.5, 5, 5, 5.5, 5, true, teamId, restaurant);
            given(teamRestaurantRepository.findByTeamIdAndIdAndUse(teamId, teamRestaurantId, true))
                    .willReturn(Optional.of(teamRestaurant));

            // when
            final TeamRestaurantResponse response =
                    teamRestaurantService.getTeamRestaurant(teamId, teamRestaurantId);

            // then
            assertThat(response.name()).isEqualTo(teamRestaurant.getRestaurant().getName());
        }

        @Test
        @DisplayName("팀 맛집이 존재하지 않으면 예외가 발생합니다.")
        void throwsWhenTeamRestaurantNotFound() {
            // given
            given(teamRestaurantRepository.findByTeamIdAndIdAndUse(teamId, teamRestaurantId, true))
                    .willReturn(Optional.empty());

            // when & then
            assertThatThrownBy(
                            () -> teamRestaurantService.getTeamRestaurant(teamId, teamRestaurantId))
                    .isInstanceOf(TeamRestaurantNotFoundException.class);
        }

        @Test
        @DisplayName("식당이 null이면 예외가 발생합니다.")
        void throwsWhenRestaurantIsNull() {
            // given
            final TeamRestaurant teamRestaurant =
                    TeamRestaurantFixture.fixture(
                            teamRestaurantId, "맛있네요", 4.5, 5, 5, 5.5, 5, true, teamId, null);

            given(teamRestaurantRepository.findByTeamIdAndIdAndUse(teamId, teamRestaurantId, true))
                    .willReturn(Optional.of(teamRestaurant));

            // when & then
            assertThatThrownBy(
                            () -> teamRestaurantService.getTeamRestaurant(teamId, teamRestaurantId))
                    .isInstanceOf(BusinessException.class)
                    .hasMessage("연결된 식당 정보가 존재하지 않습니다.");
        }

        @Test
        @DisplayName("use_yn이 'N'인 경우 조회되지 않아 예외가 발생합니다.")
        void throwsWhenUseIsFalse() {
            // given
            final Restaurant restaurant =
                    RestaurantFixture.fixture(
                            "http://place.map.kakao.com/000000",
                            "쓰지 않는 식당",
                            "서울시 어디구",
                            "서울로 456",
                            RestaurantCategory.KOREAN,
                            127.0,
                            37.0);
            final TeamRestaurant disabled =
                    TeamRestaurantFixture.fixture(
                            teamRestaurantId,
                            "쓰지 않는 식당",
                            4.0,
                            5,
                            5,
                            5.0,
                            5,
                            false,
                            teamId,
                            restaurant);
            given(teamRestaurantRepository.findByTeamIdAndIdAndUse(teamId, teamRestaurantId, true))
                    .willReturn(Optional.empty());

            // when & then
            assertThatThrownBy(
                            () -> teamRestaurantService.getTeamRestaurant(teamId, teamRestaurantId))
                    .isInstanceOf(TeamRestaurantNotFoundException.class);
        }
    }

    @Nested
    @DisplayName("팀 맛집 저장 시")
    class Save {

        private final Long userId = 1L;
        private final Long teamId = 1L;
        private final Long restaurantId = 1L;

        @Test
        @DisplayName("정상 등록된다.")
        void success() {
            // given
            final TeamRestaurantSaveRequest request =
                    new TeamRestaurantSaveRequest(restaurantId, "맛집입니다.");
            final Company company = CompanyFixture.fixture(1L, "우가우가회사", 127.0, 37.5, true);
            final Team team = TeamFixture.fixture(teamId, company, true);
            final TeamUser approvedTeamUser = TeamUserFixture.fixtureApproved(userId, team);
            final Restaurant restaurant =
                    RestaurantFixture.fixture(restaurantId, "우가우가", 127.0, 37.5, true);

            given(teamUserRepository.findWithTeamAndCompany(teamId, userId, true))
                    .willReturn(Optional.of(approvedTeamUser));

            given(restaurantRepository.findByIdAndUse(request.restaurantId(), true))
                    .willReturn(Optional.of(restaurant));

            given(
                            teamRestaurantRepository.findByTeamIdAndRestaurantIdAndUse(
                                    teamId, request.restaurantId(), true))
                    .willReturn(Optional.empty());

            given(teamRestaurantRepository.save(any())).willReturn(mock(TeamRestaurant.class));

            // when
            teamRestaurantService.save(userId, teamId, request);

            // then
            then(teamRestaurantRepository).should().save(any(TeamRestaurant.class));
            then(teamRestaurantSearchRepository).should().save(any(TeamRestaurantSearch.class));
        }

        @Test
        @DisplayName("회원이 아닌 경우 예외가 발생한다.")
        void isInvalidTeamUser() {
            // given
            final TeamRestaurantSaveRequest request =
                    new TeamRestaurantSaveRequest(restaurantId, "맛집입니다.");
            final Team team = TeamFixture.fixture(teamId, null, true);
            final TeamUser notApproved =
                    TeamUserFixture.fixture(userId, team, TeamUserStatus.PENDING, true);

            // when & then
            given(teamUserRepository.findWithTeamAndCompany(teamId, userId, true))
                    .willReturn(Optional.of(notApproved));

            assertThatThrownBy(() -> teamRestaurantService.save(userId, teamId, request))
                    .isInstanceOf(BusinessException.class)
                    .hasMessage("팀원이 아닙니다.");
        }

        @Test
        @DisplayName("이미 등록된 맛집이면 예외가 발생한다.")
        void isAlreadyExisting() {
            // given
            final TeamRestaurantSaveRequest request =
                    new TeamRestaurantSaveRequest(restaurantId, "맛집입니다.");
            final Team team = TeamFixture.fixture(teamId, null, true);
            final TeamUser approvedTeamUser = TeamUserFixture.fixtureApproved(userId, team);
            final Restaurant restaurant =
                    RestaurantFixture.fixture(restaurantId, "우가우가", 127.0, 37.5, true);

            given(teamUserRepository.findWithTeamAndCompany(teamId, userId, true))
                    .willReturn(Optional.of(approvedTeamUser));

            given(restaurantRepository.findByIdAndUse(restaurantId, true))
                    .willReturn(Optional.of(restaurant));

            given(
                            teamRestaurantRepository.findByTeamIdAndRestaurantIdAndUse(
                                    teamId, restaurantId, true))
                    .willReturn(Optional.of(mock(TeamRestaurant.class)));

            // when & then
            assertThatThrownBy(() -> teamRestaurantService.save(userId, teamId, request))
                    .isInstanceOf(BusinessException.class)
                    .hasMessage("이미 등록된 팀 맛집입니다.");
        }

        @Test
        @DisplayName("식당이 존재하지 않을 경우 예외가 발생한다")
        void restaurantNotFound() {
            // given
            final TeamRestaurantSaveRequest request =
                    new TeamRestaurantSaveRequest(restaurantId, "맛집입니다.");
            final Team team = TeamFixture.fixture(teamId, null, true);
            final TeamUser approvedTeamUser = TeamUserFixture.fixtureApproved(userId, team);

            given(teamUserRepository.findWithTeamAndCompany(teamId, userId, true))
                    .willReturn(Optional.of(approvedTeamUser));

            given(restaurantRepository.findByIdAndUse(restaurantId, true))
                    .willReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> teamRestaurantService.save(userId, teamId, request))
                    .isInstanceOf(BusinessException.class)
                    .hasMessage("식당이 존재하지 않습니다.");
        }
    }

    @Nested
    @DisplayName("팀 맛집 수정 시,")
    class UpdateTeamRestaurant {
        private final Long userId = 1L;
        private final Long teamId = 1L;
        private final Long teamRestaurantId = 1L;

        @Test
        @DisplayName("회원이 아닌 경우 예외가 발생한다.")
        void isInvalidTeamUser() {
            // given
            final TeamRestaurantUpdateRequest request = new TeamRestaurantUpdateRequest("맛집입니다.");
            final Team team = TeamFixture.fixture(teamId, null, true);
            final TeamUser notApproved =
                    TeamUserFixture.fixture(userId, team, TeamUserStatus.PENDING, true);

            // when & then
            given(teamUserRepository.findWithTeamAndCompany(teamId, userId, true))
                    .willReturn(Optional.of(notApproved));

            assertThatThrownBy(
                            () ->
                                    teamRestaurantService.updateTeamRestaurant(
                                            teamId, teamRestaurantId, userId, request))
                    .isInstanceOf(BusinessException.class)
                    .hasMessage("팀원이 아닙니다.");
        }

        @Test
        @DisplayName("성공하면 한줄평이 변경된다.")
        void updateTeamRestaurant() {
            // given
            final String newSummary = "맛집입니다.";
            final TeamRestaurantUpdateRequest request = new TeamRestaurantUpdateRequest(newSummary);

            final Restaurant restaurant =
                    RestaurantFixture.fixture(
                            "http://place.map.kakao.com/123456",
                            "우가우가 차차차",
                            "우가우가시 차차차동 24번길",
                            "우가우가 차차로 123",
                            RestaurantCategory.KOREAN,
                            127.043616,
                            37.503095);

            final TeamRestaurant teamRestaurant =
                    TeamRestaurantFixture.fixture(
                            teamRestaurantId, "맛있네요", 4.5, 5, 5, 5.5, 5, true, teamId, restaurant);

            final Team team = TeamFixture.fixture(teamId, null, true);
            final TeamUser approvedUser =
                    TeamUserFixture.fixture(userId, team, TeamUserStatus.APPROVED, true);

            given(teamUserRepository.findWithTeamAndCompany(teamId, userId, true))
                    .willReturn(Optional.of(approvedUser));

            given(teamRestaurantRepository.findByTeamIdAndIdAndUse(teamId, teamRestaurantId, true))
                    .willReturn(Optional.of(teamRestaurant));

            // when
            teamRestaurantService.updateTeamRestaurant(teamId, teamRestaurantId, userId, request);

            // then
            assertThat(teamRestaurant.getSummary()).isEqualTo(newSummary);
        }
    }

    @Nested
    @DisplayName("팀 맛집 삭제 시,")
    class DeleteTeamRestaurant {
        private final Long userId = 1L;
        private final Long teamId = 1L;
        private final Long teamRestaurantId = 1L;

        @Test
        @DisplayName("회원이 아닌 경우 예외가 발생한다.")
        void isInvalidTeamUser() {
            // given
            final Team team = TeamFixture.fixture(teamId, null, true);
            final TeamUser notApproved =
                    TeamUserFixture.fixture(userId, team, TeamUserStatus.PENDING, true);

            // when & then
            given(teamUserRepository.findWithTeamAndCompany(teamId, userId, true))
                    .willReturn(Optional.of(notApproved));

            assertThatThrownBy(
                            () ->
                                    teamRestaurantService.deleteTeamRestaurant(
                                            teamId, teamRestaurantId, userId))
                    .isInstanceOf(BusinessException.class)
                    .hasMessage("팀원이 아닙니다.");
        }

        @Test
        @DisplayName("성공하면 팀 맛집이 삭제된다(사용 여부 값 변경).")
        void deleteTeamRestaurant() {
            // given
            final Restaurant restaurant =
                    RestaurantFixture.fixture(
                            "http://place.map.kakao.com/123456",
                            "우가우가 차차차",
                            "우가우가시 차차차동 24번길",
                            "우가우가 차차로 123",
                            RestaurantCategory.KOREAN,
                            127.043616,
                            37.503095);

            final TeamRestaurant teamRestaurant =
                    TeamRestaurantFixture.fixture(
                            teamRestaurantId, "맛있네요", 4.5, 5, 5, 5.5, 5, true, teamId, restaurant);

            final boolean before = teamRestaurant.isUse();
            final Team team = TeamFixture.fixture(teamId, null, true);
            final TeamUser approvedUser =
                    TeamUserFixture.fixture(userId, team, TeamUserStatus.APPROVED, true);

            given(teamUserRepository.findWithTeamAndCompany(teamId, userId, true))
                    .willReturn(Optional.of(approvedUser));

            given(teamRestaurantRepository.findByTeamIdAndIdAndUse(teamId, teamRestaurantId, true))
                    .willReturn(Optional.of(teamRestaurant));

            // when
            teamRestaurantService.deleteTeamRestaurant(teamId, teamRestaurantId, userId);

            // then
            assertThat(teamRestaurant.isUse()).isNotEqualTo(before);
        }
    }
}
