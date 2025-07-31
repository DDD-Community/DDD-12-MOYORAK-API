package com.moyorak.api.review.dto;

public record LabelPair(String servingLabel, String waitingLabel) {
    public static final LabelPair EMPTY = new LabelPair("기타", "기타");
}
