package com.moyorak.api.team.domain;

import com.moyorak.api.restaurant.domain.Restaurant;
import com.moyorak.infra.orm.AuditInformation;
import com.moyorak.infra.orm.BooleanYnConverter;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Comment;

@Getter
@Entity
@Table(name = "team_restaurant")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class TeamRestaurant extends AuditInformation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, columnDefinition = "bigint")
    private Long id;

    @Comment("한줄 소개")
    @Column(name = "summary", columnDefinition = "varchar(32)")
    private String summary;

    @Comment("리뷰 평균 점수")
    @Column(name = "average_review_score", columnDefinition = "double")
    private Double averageReviewScore;

    @Comment("평균 음식 나오는 시간")
    @Column(name = "average_serving_time", columnDefinition = "int")
    private Integer averageServingTime;

    @Comment("평균 대기 시간")
    @Column(name = "average_waiting_time", columnDefinition = "int")
    private Integer averageWaitingTime;

    @Comment("팀에서 맛집까지 거리")
    @Column(name = "distance_from_team", columnDefinition = "double")
    private Double distanceFromTeam;

    @Comment("리뷰 갯수")
    @Column(name = "review_count", columnDefinition = "int")
    private Integer reviewCount;

    @Comment("사용 여부")
    @Convert(converter = BooleanYnConverter.class)
    @Column(name = "use_yn", nullable = false, columnDefinition = "char(1)")
    private boolean use = true;

    @Comment("team 고유 ID")
    @Column(name = "team_id", nullable = false, columnDefinition = "bigint")
    private Long teamId;

    @Comment("식당 고유 ID")
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "restaurant_id", nullable = false)
    private Restaurant restaurant;

    public boolean isRestaurantNull() {
        return this.restaurant == null;
    }
}
