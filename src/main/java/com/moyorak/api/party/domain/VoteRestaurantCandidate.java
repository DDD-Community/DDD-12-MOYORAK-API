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
@Table(name = "vote_restaurant_candidate")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class VoteRestaurantCandidate extends AuditInformation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, columnDefinition = "bigint")
    private Long id;

    @Comment("팀 맛집 고유 ID")
    @Column(name = "team_restaurant_id", nullable = false, columnDefinition = "bigint")
    private Long teamRestaurantId;

    @Comment("투표 고유 ID")
    @Column(name = "vote_id", nullable = false, columnDefinition = "bigint")
    private Long voteId;

    @Comment("사용 여부")
    @Convert(converter = BooleanYnConverter.class)
    @Column(name = "use_yn", nullable = false, columnDefinition = "char(1)")
    private boolean use = true;
}
