package com.moyorak.api.history.domain;

import java.time.LocalDateTime;
import org.springframework.test.util.ReflectionTestUtils;

public class ViewHistoryFixture {

    public static ViewHistory fixture(
            final Long id,
            final Long teamId,
            final Long userId,
            final Long teamRestaurantId,
            final LocalDateTime createdDate) {
        ViewHistory viewHistory = new ViewHistory();

        ReflectionTestUtils.setField(viewHistory, "id", id);
        ReflectionTestUtils.setField(viewHistory, "teamId", teamId);
        ReflectionTestUtils.setField(viewHistory, "userId", userId);
        ReflectionTestUtils.setField(viewHistory, "teamRestaurantId", teamRestaurantId);
        ReflectionTestUtils.setField(viewHistory, "createdDate", createdDate);

        return viewHistory;
    }
}
