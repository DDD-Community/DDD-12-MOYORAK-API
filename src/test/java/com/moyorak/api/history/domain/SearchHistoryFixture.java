package com.moyorak.api.history.domain;

import java.time.LocalDateTime;
import org.springframework.test.util.ReflectionTestUtils;

public class SearchHistoryFixture {

    public static SearchHistory fixture(
            final Long id, final String keyword, final LocalDateTime createdDate) {
        SearchHistory searchHistory = new SearchHistory();

        ReflectionTestUtils.setField(searchHistory, "id", id);
        ReflectionTestUtils.setField(searchHistory, "keyword", keyword);
        ReflectionTestUtils.setField(searchHistory, "createdDate", createdDate);

        return searchHistory;
    }

    public static SearchHistory fixture(
            final Long id,
            final String keyword,
            final boolean use,
            final LocalDateTime createdDate) {
        SearchHistory searchHistory = new SearchHistory();

        ReflectionTestUtils.setField(searchHistory, "id", id);
        ReflectionTestUtils.setField(searchHistory, "keyword", keyword);
        ReflectionTestUtils.setField(searchHistory, "use", use);
        ReflectionTestUtils.setField(searchHistory, "createdDate", createdDate);

        return searchHistory;
    }

    public static SearchHistory fixture(
            final Long id,
            final String keyword,
            final Long userId,
            final boolean use,
            final LocalDateTime createdDate) {
        SearchHistory searchHistory = new SearchHistory();

        ReflectionTestUtils.setField(searchHistory, "id", id);
        ReflectionTestUtils.setField(searchHistory, "keyword", keyword);
        ReflectionTestUtils.setField(searchHistory, "use", use);
        ReflectionTestUtils.setField(searchHistory, "userId", userId);
        ReflectionTestUtils.setField(searchHistory, "createdDate", createdDate);

        return searchHistory;
    }
}
