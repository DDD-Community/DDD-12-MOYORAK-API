package com.moyorak.api.team.domain;

import com.moyorak.config.exception.BusinessException;

public class NotTeamUserException extends BusinessException {
    public NotTeamUserException() {
        super("해당 팀의 팀원이 아닙니다.");
    }
}
