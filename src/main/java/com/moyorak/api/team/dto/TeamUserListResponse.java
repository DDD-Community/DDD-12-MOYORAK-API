package com.moyorak.api.team.dto;

import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import org.springframework.data.domain.Page;

@Schema(title = "팀원 목록 조회 응답 DTO")
public record TeamUserListResponse(
        @Schema(description = "한 페이지 아이템 갯수", example = "10") int size,
        @Schema(description = "현재 페이지", example = "1") int currentPage,
        @Schema(description = "총 리스트 갯수", example = "100") long totalCount,
        @Schema(description = "팀 이름", example = "WEB2") String teamName,
        @ArraySchema(
                        schema =
                                @Schema(
                                        description = "팀원 조회",
                                        implementation = TeamUserResponse.class),
                        arraySchema = @Schema(description = "팀원 조회 응답 리스트"))
                List<TeamUserResponse> teamUsers) {
    public static TeamUserListResponse from(
            Page<TeamUserResponse> teamUserResponses, String teamName) {
        return new TeamUserListResponse(
                teamUserResponses.getSize(),
                teamUserResponses.getNumber() + 1,
                teamUserResponses.getTotalElements(),
                teamName,
                teamUserResponses.getContent());
    }
}
