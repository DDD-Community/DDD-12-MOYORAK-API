package com.moyorak.api.party.domain;

import com.moyorak.infra.orm.CommonEnum;
import lombok.Getter;

@Getter
public enum VoteStatus implements CommonEnum {
    READY("투표 전"),
    VOTING("투표 중"),
    DONE("종료");

    private final String description;

    VoteStatus(String description) {
        this.description = description;
    }

    public boolean isDone() {
        return this == DONE;
    }
}
