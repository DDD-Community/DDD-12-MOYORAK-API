package com.moyorak.api.party.domain;

import com.moyorak.infra.orm.AuditInformation;
import com.moyorak.infra.orm.BooleanYnConverter;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Comment;

@Getter
@Entity
@Table(name = "selection_vote_info")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class SelectionVoteInfo extends AuditInformation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, columnDefinition = "bigint")
    private Long id;

    @Comment("투표 시작 시간")
    @Column(name = "start_date", nullable = false)
    private LocalDateTime startDate;

    @Comment("투표 마감 시간")
    @Column(name = "expired_date", nullable = false)
    private LocalDateTime expiredDate;

    @Comment("식사 시간")
    @Column(name = "meal_date", nullable = false)
    private LocalDateTime mealDate;

    @Comment("투표 고유 ID")
    @Column(name = "vote_id", nullable = false, columnDefinition = "bigint")
    private Long voteId;

    @Comment("사용 여부")
    @Convert(converter = BooleanYnConverter.class)
    @Column(name = "use_yn", nullable = false, columnDefinition = "char(1)")
    private boolean use = true;

    @Builder(access = AccessLevel.PRIVATE)
    private SelectionVoteInfo(
            LocalDateTime startDate,
            LocalDateTime expiredDate,
            LocalDateTime mealDate,
            Long voteId,
            boolean use) {
        this.startDate = startDate;
        this.expiredDate = expiredDate;
        this.mealDate = mealDate;
        this.voteId = voteId;
        this.use = use;
    }

    public static SelectionVoteInfo generate(
            final Long voteId,
            final LocalDate date,
            final LocalTime startDate,
            final LocalTime expiredDate,
            final LocalTime mealDate) {
        return SelectionVoteInfo.builder()
                .voteId(voteId)
                .startDate(LocalDateTime.of(date, startDate))
                .expiredDate(LocalDateTime.of(date, expiredDate))
                .mealDate(LocalDateTime.of(date, mealDate))
                .use(true)
                .build();
    }
}
