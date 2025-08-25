package com.moyorak.api.auth.dto;

public class UserReviewRequestFixture {
    public static UserReviewRequest fixture(Integer currentPage, Integer size) {
        return new UserReviewRequest(currentPage, size);
    }
}
