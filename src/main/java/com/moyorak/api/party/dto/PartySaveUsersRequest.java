package com.moyorak.api.party.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import java.util.List;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Schema(title = "[파티] 파티 생성 요청 DTO - 팀원 정보")
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class PartySaveUsersRequest {

    @Valid
    @Schema(description = "선택 팀원 리스트")
    private List<PartySaveUserRequest> ids;
}
