package com.moyorak.api.team.domain;

import com.moyorak.api.restaurant.domain.Restaurant;
import com.moyorak.infra.orm.AuditInformation;
import com.moyorak.infra.orm.BooleanYnConverter;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Comment;

@Getter
@Entity
@Table(name = "team_restaurant_search")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class TeamRestaurantSearch extends AuditInformation {

    @Id
    @Column(name = "team_restaurant_id", nullable = false, columnDefinition = "bigint")
    private Long teamRestaurantId;

    @Comment("팀 고유 ID")
    @Column(name = "team_id", nullable = false, columnDefinition = "bigint")
    private Long teamId;

    @Comment("식당 이름")
    @Column(name = "name", nullable = false, columnDefinition = "varchar(255)")
    private String name;

    @Comment("식당 경도")
    @Column(name = "longitude", nullable = false, columnDefinition = "double")
    private double longitude;

    @Comment("사용 위도")
    @Column(name = "latitude", nullable = false, columnDefinition = "double")
    private double latitude;

    @Comment("리뷰 평균 점수")
    @Column(name = "average_review_score", nullable = false, columnDefinition = "double")
    private double averageReviewScore;

    @Comment("팀에서 맛집까지 거리")
    @Column(name = "distance_from_team", nullable = false, columnDefinition = "double")
    private double distanceFromTeam;

    @Comment("사용 여부")
    @Convert(converter = BooleanYnConverter.class)
    @Column(name = "use_yn", nullable = false, columnDefinition = "char(1)")
    private boolean use = true;

    public void toggleUse() {
        this.use = !this.use;
    }

    public static TeamRestaurantSearch from(TeamRestaurant teamRestaurant, Restaurant restaurant) {
        TeamRestaurantSearch teamRestaurantSearch = new TeamRestaurantSearch();

        teamRestaurantSearch.teamRestaurantId = teamRestaurant.getId();
        teamRestaurantSearch.teamId = teamRestaurant.getTeamId();
        teamRestaurantSearch.name = restaurant.getName();
        teamRestaurantSearch.longitude = restaurant.getLongitude();
        teamRestaurantSearch.latitude = restaurant.getLatitude();
        teamRestaurantSearch.distanceFromTeam = teamRestaurant.getDistanceFromTeam();
        teamRestaurantSearch.averageReviewScore = teamRestaurant.getAverageReviewScore();

        return teamRestaurantSearch;
    }
}
