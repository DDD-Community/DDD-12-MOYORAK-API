package com.moyorak.api.history.repository;

import com.moyorak.api.history.domain.SearchHistory;
import jakarta.persistence.QueryHint;
import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.QueryHints;

public interface SearchHistoryRepository extends JpaRepository<SearchHistory, Long> {

    @QueryHints(
            @QueryHint(
                    name = "org.hibernate.comment",
                    value =
                            "SearchHistoryRepository.findAllByUserIdAndTeamIdAndUse : 팀 맛집 검색 기록을 조회합니다."))
    List<SearchHistory> findAllByUserIdAndTeamIdAndUse(
            Long userId, Long teamId, boolean use, Pageable pageable);
}
