package com.moyorak.api.team.dto;

import com.moyorak.api.team.domain.TeamUserStatus;
import com.moyorak.global.domain.ListRequest;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

@ParameterObject
@Schema(title = "[팀 멤버] 팀 멤버 리스트 조회 요청 DTO")
@Getter
public class TeamUserListRequest extends ListRequest {

    @NotNull(message = "status는 필수입니다")
    @Parameter(description = "팀 멤버 상태", example = "APPROVED")
    private final TeamUserStatus status;

    protected TeamUserListRequest(TeamUserStatus status, Integer size, Integer currentPage) {
        super(size, currentPage);
        this.status = status;
    }

    public Pageable toRecentPageable() {
        return PageRequest.of(
                super.getCurrentPage() - 1, super.getSize(), Sort.by(Sort.Direction.DESC, "id"));
    }
}
