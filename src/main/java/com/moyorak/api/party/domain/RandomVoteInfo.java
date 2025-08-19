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
@Table(name = "random_vote_info")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RandomVoteInfo extends AuditInformation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, columnDefinition = "bigint")
    private Long id;

    @Comment("랜덤 추첨 시간")
    @Column(name = "random_date", nullable = false)
    private LocalDateTime randomDate;

    @Comment("식사 시간")
    @Column(name = "meal_date", nullable = false)
    private LocalDateTime mealDate;

    @Comment("투표 고유 ID")
    @Column(name = "vote_id", nullable = false, columnDefinition = "bigint")
    private Long voteId;

    @Comment("선정된 식당 후보 ID")
    @Column(name = "selected_candidate_id", columnDefinition = "bigint")
    private Long selectedCandidateId;

    @Comment("사용 여부")
    @Convert(converter = BooleanYnConverter.class)
    @Column(name = "use_yn", nullable = false, columnDefinition = "char(1)")
    private boolean use = true;

    @Builder(access = AccessLevel.PRIVATE)
    private RandomVoteInfo(
            LocalDateTime randomDate,
            LocalDateTime mealDate,
            Long voteId,
            Long selectedCandidateId,
            boolean use) {
        this.randomDate = randomDate;
        this.mealDate = mealDate;
        this.voteId = voteId;
        this.selectedCandidateId = selectedCandidateId;
        this.use = use;
    }

    public static RandomVoteInfo generate(
            final Long voteId, final LocalDate date, final LocalTime randomDate) {
        return RandomVoteInfo.builder()
                .voteId(voteId)
                .randomDate(LocalDateTime.of(date, randomDate))
                .use(true)
                .build();
    }

    public void confirmRandomCandidate(final Long candidateId) {
        selectedCandidateId = candidateId;
    }

    public boolean hasSelectedCandidate() {
        return selectedCandidateId != null;
    }
}
