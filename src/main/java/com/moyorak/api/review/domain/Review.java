package com.moyorak.api.review.domain;

import com.moyorak.infra.orm.AuditInformation;
import com.moyorak.infra.orm.BooleanYnConverter;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.util.Objects;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Comment;

@Getter
@Entity
@Table(name = "review")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Review extends AuditInformation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Comment("평점")
    @Column(name = "score", nullable = false)
    private Integer score;

    @Comment("음식 나오는 시간")
    @Column(name = "serving_time", nullable = false)
    private Integer servingTime;

    @Comment("대기 시간")
    @Column(name = "waiting_time", nullable = false)
    private Integer waitingTime;

    @Comment("추가 텍스트")
    @Column(name = "extra_text", nullable = false, columnDefinition = "varchar(512)")
    private String extraText;

    @Comment("사용 여부")
    @Convert(converter = BooleanYnConverter.class)
    @Column(name = "use_yn", nullable = false, columnDefinition = "char(1)")
    private boolean use = true;

    @Comment("사용자 고유 ID")
    @Column(name = "member_id", nullable = false)
    private Long userId;

    @Comment("식당 고유 ID")
    @Column(name = "team_restaurant_id", nullable = false)
    private Long teamRestaurantId;

    public boolean isMine(final Long userId) {
        return this.userId.equals(userId);
    }

    public void toggleUse() {
        this.use = !this.use;
    }

    public void updateReview(
            final String extraText,
            final Integer servingTime,
            final Integer waitingTime,
            final Integer score) {
        if (!Objects.equals(this.extraText, extraText)) {
            this.extraText = extraText;
        }
        if (!Objects.equals(this.servingTime, servingTime)) {
            this.servingTime = servingTime;
        }
        if (!Objects.equals(this.waitingTime, waitingTime)) {
            this.waitingTime = waitingTime;
        }
        if (!Objects.equals(this.score, score)) {
            this.score = score;
        }
    }

    @Builder(access = AccessLevel.PRIVATE)
    private Review(
            Long id,
            Integer score,
            Integer servingTime,
            Integer waitingTime,
            String extraText,
            Long userId,
            Long teamRestaurantId) {
        this.id = id;
        this.score = score;
        this.servingTime = servingTime;
        this.waitingTime = waitingTime;
        this.extraText = extraText;
        this.userId = userId;
        this.teamRestaurantId = teamRestaurantId;
    }

    public static Review create(
            final Integer score,
            final Integer servingTime,
            final Integer waitingTime,
            final String extraText,
            final Long userId,
            final Long teamRestaurantId) {
        return Review.builder()
                .score(score)
                .servingTime(servingTime)
                .waitingTime(waitingTime)
                .extraText(extraText)
                .userId(userId)
                .teamRestaurantId(teamRestaurantId)
                .build();
    }
}
