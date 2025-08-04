package com.moyorak.api.team.domain;

import com.moyorak.infra.orm.CommonEnum;
import lombok.Getter;

@Getter
public enum TeamUserStatus implements CommonEnum {
    PENDING("대기"),
    APPROVED("가입"),
    REJECTED("거절"),
    WITHDRAWN("탈퇴");

    private final String description;

    TeamUserStatus(String description) {
        this.description = description;
    }
}
