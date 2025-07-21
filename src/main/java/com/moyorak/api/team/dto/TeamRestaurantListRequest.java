package com.moyorak.api.team.dto;

import com.moyorak.api.team.domain.SortOption;
import com.moyorak.global.domain.ListRequest;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

@Getter
@Schema(title = "[팀 맛집] 팀 맛집 목록 조회 요청 DTO")
public class TeamRestaurantListRequest extends ListRequest {
    @NotNull(message = "정렬 조건을 입력해주세요.")
    @Schema(description = "정렬 조건", example = "DISTANCE")
    private final SortOption sortOption;

    protected TeamRestaurantListRequest(SortOption sortOption, Integer size, Integer currentPage) {
        super(size, currentPage);
        this.sortOption = sortOption;
    }

    @Override
    public Pageable toPageable() {
        return PageRequest.of(super.getCurrentPage() - 1, super.getSize(), sortOption.toSort());
    }
}
