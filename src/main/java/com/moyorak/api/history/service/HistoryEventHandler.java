package com.moyorak.api.history.service;

import com.moyorak.api.history.domain.SearchHistory;
import com.moyorak.api.history.domain.ViewHistory;
import com.moyorak.api.history.repository.SearchHistoryRepository;
import com.moyorak.api.history.repository.ViewHistoryRepository;
import com.moyorak.api.team.dto.TeamRestaurantSearchEvent;
import com.moyorak.api.team.dto.TeamRestaurantViewEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Service
@RequiredArgsConstructor
public class HistoryEventHandler {

    private final SearchHistoryRepository searchHistoryRepository;
    private final ViewHistoryRepository viewHistoryRepository;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleSearchEvent(final TeamRestaurantSearchEvent event) {

        if (isExistingSearchHistory(event)) {
            return;
        }

        final SearchHistory searchHistory =
                SearchHistory.create(event.keyword(), event.teamId(), event.userId());

        searchHistoryRepository.save(searchHistory);
    }

    private boolean isExistingSearchHistory(TeamRestaurantSearchEvent event) {
        return searchHistoryRepository.existsByTeamIdAndUserIdAndKeywordAndUseTrue(
                event.teamId(), event.userId(), event.keyword());
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleViewEvent(final TeamRestaurantViewEvent event) {

        if (isExistingViewHistory(event)) {
            return;
        }

        final ViewHistory viewHistory =
                ViewHistory.create(event.userId(), event.teamId(), event.teamRestaurantId());
        viewHistoryRepository.save(viewHistory);
    }

    private boolean isExistingViewHistory(TeamRestaurantViewEvent event) {
        return viewHistoryRepository.existsByTeamIdAndUserIdAndTeamRestaurantIdAndUseTrue(
                event.teamId(), event.userId(), event.teamRestaurantId());
    }
}
