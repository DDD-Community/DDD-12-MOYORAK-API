package com.moyorak.api.team.dto;

import com.moyorak.api.company.domain.Company;
import com.moyorak.api.team.domain.Team;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record TeamSaveRequest(
        @NotBlank(message = "팀 이름을 입력해주세요.")
                @Size(min = 1, max = 255, message = "키워드는 {min}자 이상 {max}자 이하여야 합니다.")
                @Schema(description = "팀 이름", example = "Backend 파트")
                String name) {
    public Team toEntity(final Company company) {
        return Team.create(name, company);
    }
}
