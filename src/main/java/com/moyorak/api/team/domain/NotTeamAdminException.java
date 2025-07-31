package com.moyorak.api.team.domain;

import com.moyorak.config.exception.BusinessException;

public class NotTeamAdminException extends BusinessException {
    public NotTeamAdminException() {
        super("팀 관리자가 아닙니다.");
    }
}
