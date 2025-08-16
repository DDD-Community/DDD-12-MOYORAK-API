package com.moyorak.api.party.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Schema(title = "[파티] 파티 생성 요청 DTO - 팀원 정보")
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class PartySaveUserRequest {

    @NotNull(message = "팀원 ID는 필수입니다.")
    @Schema(description = "팀원 ID", example = "1")
    private Long userId;
}
