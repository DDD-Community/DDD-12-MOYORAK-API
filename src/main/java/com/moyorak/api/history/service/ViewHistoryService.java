package com.moyorak.api.history.service;

import com.moyorak.api.history.domain.ViewHistory;
import com.moyorak.api.history.dto.ViewHistoryRequest;
import com.moyorak.api.history.dto.ViewHistorySummaries;
import com.moyorak.api.history.repository.ViewHistoryRepository;
import com.moyorak.config.exception.BusinessException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ViewHistoryService {

    private final ViewHistoryRepository viewHistoryRepository;

    @Transactional(readOnly = true)
    public ViewHistorySummaries getViewHistorySummaries(final ViewHistoryRequest request) {
        final List<ViewHistory> viewHistories =
                viewHistoryRepository.findAllByUserIdAndTeamIdAndUse(
                        request.userId(), request.teamId(), true, request.toRecentPageable());
        return ViewHistorySummaries.from(viewHistories);
    }

    @Transactional
    public void deleteViewHistory(final Long viewHistoryId, final Long userId) {
        ViewHistory viewHistory =
                viewHistoryRepository
                        .findByIdAndUserIdAndUse(viewHistoryId, userId, true)
                        .orElseThrow(() -> new BusinessException("조회 기록이 존재하지 않습니다."));
        viewHistory.delete();
    }
}
