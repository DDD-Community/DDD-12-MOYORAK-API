package com.moyorak.api.history.domain;

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
@Table(name = "view_history")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ViewHistory extends AuditInformation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Comment("사용 여부")
    @Convert(converter = BooleanYnConverter.class)
    @Column(name = "use_yn", nullable = false, columnDefinition = "char(1)")
    private boolean use = true;

    @Comment("팀 고유 ID")
    @Column(name = "team_id", nullable = false)
    private Long teamId;

    @Comment("유저 고유 ID")
    @Column(name = "member_id", nullable = false)
    private Long userId;

    @Comment("팀 고유 ID")
    @Column(name = "team_restaurant_id", nullable = false)
    private Long teamRestaurantId;

    public static ViewHistory create(
            final Long userId, final Long teamId, final Long teamRestaurantId) {
        ViewHistory viewHistory = new ViewHistory();

        viewHistory.userId = userId;
        viewHistory.teamId = teamId;
        viewHistory.teamRestaurantId = teamRestaurantId;

        return viewHistory;
    }
}
