package com.moyorak.api.history.repository;

import com.moyorak.api.history.domain.ViewHistory;
import jakarta.persistence.QueryHint;
import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.QueryHints;

public interface ViewHistoryRepository extends JpaRepository<ViewHistory, Long> {

    @QueryHints(
            @QueryHint(
                    name = "org.hibernate.comment",
                    value =
                            "ViewHistoryRepository.findAllByUserIdAndTeamIdAndUse : 팀 맛집 상세 조회 기록 리스트를 조회합니다."))
    List<ViewHistory> findAllByUserIdAndTeamIdAndUse(
            Long userId, Long teamId, boolean use, Pageable pageable);
}
