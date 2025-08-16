package com.moyorak.api.party.domain;

import com.moyorak.infra.orm.AuditInformation;
import com.moyorak.infra.orm.BooleanYnConverter;
import com.moyorak.infra.orm.VoteStatusConverter;
import com.moyorak.infra.orm.VoteTypeConverter;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Comment;

@Getter
@Entity
@Table(name = "vote")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Vote extends AuditInformation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, columnDefinition = "bigint")
    private Long id;

    @Comment("파티 고유 ID")
    @Column(name = "party_id", nullable = false, columnDefinition = "bigint")
    private Long partyId;

    @Comment("투표 방식")
    @Convert(converter = VoteTypeConverter.class)
    @Column(name = "type", nullable = false, columnDefinition = "varchar(16)")
    private VoteType type;

    @Comment("투표 상태")
    @Convert(converter = VoteStatusConverter.class)
    @Column(name = "status", nullable = false, columnDefinition = "varchar(16)")
    private VoteStatus status;

    @Comment("사용 여부")
    @Convert(converter = BooleanYnConverter.class)
    @Column(name = "use_yn", nullable = false, columnDefinition = "char(1)")
    private boolean use = true;

    public void changeStatusByNowForSelectionVote(
            final LocalDateTime now, final SelectionVoteInfo info) {
        if (now.isBefore(info.getStartDate())) {
            status = VoteStatus.READY;
            return;
        }

        if (now.isBefore(info.getExpiredDate())) {
            status = VoteStatus.VOTING;
            return;
        }

        status = VoteStatus.DONE;
    }

    public void changeStatusByNowForRandomVote(final LocalDateTime now, final RandomVoteInfo info) {
        status = now.isBefore(info.getRandomDate()) ? VoteStatus.READY : VoteStatus.DONE;
    }

    public boolean isDone() {
        return status.isDone();
    }

    public boolean isSelectVote() {
        return type == VoteType.SELECT;
    }
}
