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
@Table(name = "search_history")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class SearchHistory extends AuditInformation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Comment("검색 키워드")
    @Column(name = "keyword", nullable = false, columnDefinition = "varchar(255)")
    private String keyword;

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

    public static SearchHistory create(String keyword, Long teamId, Long userId) {
        SearchHistory searchHistory = new SearchHistory();

        searchHistory.keyword = keyword;
        searchHistory.teamId = teamId;
        searchHistory.userId = userId;

        return searchHistory;
    }

    public void delete() {
        use = false;
    }
}
