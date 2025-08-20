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
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Comment;

@Getter
@Entity
@Table(name = "party")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Party extends AuditInformation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Comment("팀 고유 ID")
    @Column(name = "team_id", nullable = false, columnDefinition = "bigint")
    private Long teamId;

    @Comment("파티 이름")
    @Column(name = "title", nullable = false, columnDefinition = "varchar(512)")
    private String title;

    @Comment("파티 설명")
    @Column(name = "content", nullable = false, columnDefinition = "varchar(512)")
    private String content;

    @Comment("파티 참여 가능 여부")
    @Convert(converter = BooleanYnConverter.class)
    @Column(name = "attendable", nullable = false, columnDefinition = "char(1)")
    private boolean attendable;

    @Comment("사용 여부")
    @Convert(converter = BooleanYnConverter.class)
    @Column(name = "use_yn", nullable = false, columnDefinition = "char(1)")
    private boolean use = true;

    @Builder(access = AccessLevel.PRIVATE)
    Party(Long teamId, String title, String content, boolean use) {
        this.teamId = teamId;
        this.title = title;
        this.content = content;
        this.use = use;
    }

    public static Party create(final Long teamId, final String title, final String content) {
        return Party.builder().teamId(teamId).title(title).content(content).use(true).build();
    }
}
