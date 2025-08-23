package com.moyorak.api.auth.service;

import com.moyorak.api.auth.dto.UserOrganisationResponse;
import com.moyorak.api.team.service.TeamUserService;
import com.moyorak.config.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserFacade {

    private final UserService userService;

    private final AuthService authService;

    private final MealTagService mealTagService;

    private final TeamUserService teamUserService;

    /**
     * 회원 탈퇴를 처리합니다. <br>
     * <br>
     * 회원 탈퇴 시 다음과 같은 작업을 수행합니다:<br>
     * 1. 팀에 관리자로 속해 있는지 검증<br>
     * 2. 회원 정보 제거<br>
     * 3. 과거 팀 소속 정보 제거<br>
     * 4. 비선호 + 알러지 정보 초기화<br>
     * 5. 토큰 초기화
     *
     * @param userId 회원 고유 ID
     */
    @Transactional
    public void unregister(final Long userId) {
        // 1. 팀에 관리자로 속해 있는지 검증
        if (teamUserService.isTeamAdmin(userId)) {
            throw new BusinessException("팀 관리자는 탈퇴할 수 없습니다.");
        }

        // 2. 회원 정보 제거
        userService.unregister(userId);

        // 3. 과거 팀 소속 정보 제거
        teamUserService.clear(userId);

        // 4. 비선호 + 알러지 정보 초기화
        mealTagService.clear(userId);

        // 5. 토큰 초기화
        authService.signOut(userId);
    }

    /**
     * 입력된 ID의 회사, 팀 ID를 조회합니다.
     *
     * @param userId 회원 고유 ID
     * @return
     */
    @Transactional(readOnly = true)
    public UserOrganisationResponse getMe(final Long userId) {
        return teamUserService.getTeamId(userId);
    }
}
