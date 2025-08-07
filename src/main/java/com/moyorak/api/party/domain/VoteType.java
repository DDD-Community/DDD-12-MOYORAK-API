package com.moyorak.api.party.domain;

import com.moyorak.infra.orm.CommonEnum;
import lombok.Getter;

@Getter
public enum VoteType implements CommonEnum {
    SELECT("선택"),
    RANDOM("랜덤");

    private final String description;

    VoteType(String description) {
        this.description = description;
    }
}
