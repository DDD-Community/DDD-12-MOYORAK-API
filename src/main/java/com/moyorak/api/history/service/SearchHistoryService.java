package com.moyorak.api.history.service;

import com.moyorak.api.history.domain.SearchHistory;
import com.moyorak.api.history.dto.SearchHistoryListResponse;
import com.moyorak.api.history.dto.SearchHistoryRequest;
import com.moyorak.api.history.repository.SearchHistoryRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SearchHistoryService {

    private final SearchHistoryRepository searchHistoryRepository;

    public SearchHistoryListResponse getSearchHistories(final SearchHistoryRequest request) {
        final List<SearchHistory> searchHistories =
                searchHistoryRepository.findAllByUserIdAndTeamIdAndUse(
                        request.userId(), request.teamId(), true, request.toRecentPageable());
        return SearchHistoryListResponse.from(searchHistories);
    }
}
