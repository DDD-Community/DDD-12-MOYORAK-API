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
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Comment;

@Getter
@Entity
@Table(name = "vote_record")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class VoteRecord extends AuditInformation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, columnDefinition = "bigint")
    private Long id;

    @Comment("투표 고유 ID")
    @Column(name = "vote_id", nullable = false, columnDefinition = "bigint")
    private Long voteId;

    @Comment("식당 후보 고유 ID")
    @Column(name = "vote_restaurant_candidate_id", nullable = false, columnDefinition = "bigint")
    private Long voteRestaurantCandidateId;

    @Comment("파티 참가자 고유 ID")
    @Column(name = "attendee_id", nullable = false, columnDefinition = "bigint")
    private Long attendeeId;

    @Comment("사용 여부")
    @Convert(converter = BooleanYnConverter.class)
    @Column(name = "use_yn", nullable = false, columnDefinition = "char(1)")
    private boolean use = true;
}
