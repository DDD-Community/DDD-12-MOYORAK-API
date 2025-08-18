package com.moyorak.api.party.domain;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.moyorak.infra.orm.CommonEnum;
import lombok.Getter;
import org.springframework.util.StringUtils;

@Getter
public enum VoteType implements CommonEnum {
    SELECT("선택"),
    RANDOM("랜덤");

    private final String description;

    VoteType(String description) {
        this.description = description;
    }

    @JsonCreator
    public static VoteType from(final String input) {
        if (!StringUtils.hasText(input)) {
            return null;
        }

        for (VoteType voteType : VoteType.values()) {
            if (input.equals(voteType.name())) {
                return voteType;
            }
        }
        return null;
    }

    public boolean isSelect() {
        return this == VoteType.SELECT;
    }
}
