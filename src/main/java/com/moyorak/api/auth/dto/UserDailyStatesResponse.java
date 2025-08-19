package com.moyorak.api.auth.dto;

import com.moyorak.api.auth.domain.State;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.util.ObjectUtils;

@Getter
@Schema(title = "혼밥 상태를 포함한 회원 정보 응답 DTO")
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class UserDailyStatesResponse {

    @Schema(description = "회원 고유 ID", example = "1")
    private Long userId;

    @Schema(description = "이름", example = "홍길동")
    private String name;

    @Schema(description = "혼밥 상태, ON/OFF", example = "OFF")
    private State state;

    public UserDailyStatesResponse(Long userId, String name, State state) {
        this.userId = userId;
        this.name = name;
        this.state = ObjectUtils.isEmpty(state) ? State.OFF : state;
    }
}
