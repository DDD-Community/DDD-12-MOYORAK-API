package com.moyorak.api.review.domain;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class ReviewTest {

    @Test
    @DisplayName("본인 리뷰인지 확인한다.")
    void isMine() {
        // given
        Long userId = 100L;
        final Review review = ReviewFixture.fixture(1L, 10, 5, 5, "맛있어요!", userId, 1L);

        // when
        final boolean resultSameUser = review.isMine(100L);
        final boolean resultOtherUser = review.isMine(200L);

        // then
        assertThat(resultSameUser).isTrue();
        assertThat(resultOtherUser).isFalse();
    }

    @Test
    @DisplayName("리뷰 값을 수정한다.")
    void update() {
        // given
        Long userId = 100L;
        final Review review = ReviewFixture.fixture(1L, 10, 5, 5, "맛있어요!", userId, 1L);

        // when
        review.updateReview("맛있어유", 15, 15, 5);

        // then
        assertThat(review.getExtraText()).isEqualTo("맛있어유");
        assertThat(review.getServingTime()).isEqualTo(15);
        assertThat(review.getWaitingTime()).isEqualTo(15);
        assertThat(review.getScore()).isEqualTo(5);
    }
}
