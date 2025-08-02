package com.moyorak.api.auth.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.moyorak.api.auth.domain.MealTag;
import com.moyorak.api.auth.domain.MealTagType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotNull;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import org.springframework.util.ObjectUtils;

@Schema(title = "[마이] 알러지, 비선호 음식 상세 저장 요청 DTO")
public record MealTagSaveRequest(
        @NotNull @Schema(description = "회원 고유 ID", example = "13") Long userId,
        @Valid List<MealTagDetailsSaveRequest> details) {
    public static final long MAX_ITEMS_PER_TYPE = 10;

    public MealTagSaveRequest {
        // 중복 제거를 하기위해 생성자에 입력 받은 List를 Set으로 대입하여 처리
        details =
                ObjectUtils.isEmpty(details)
                        ? List.of()
                        : List.copyOf(new LinkedHashSet<>(details));
    }

    @JsonIgnore
    @AssertTrue(message = "각 type 별로 최대 " + MAX_ITEMS_PER_TYPE + "개 까지만 등록할 수 있습니다.")
    public boolean isTypeItemCountValid() {
        if (ObjectUtils.isEmpty(details)) {
            return true;
        }

        Map<MealTagType, Long> typeCountMap =
                details.stream()
                        .filter(Objects::nonNull)
                        .collect(
                                Collectors.groupingBy(
                                        MealTagDetailsSaveRequest::type, Collectors.counting()));

        return typeCountMap.values().stream().allMatch(count -> count <= MAX_ITEMS_PER_TYPE);
    }

    public List<MealTag> toEntities() {
        return details.stream().map(it -> it.toEntity(userId)).toList();
    }

    public long getCountByType(final MealTagType type) {
        return details.stream().filter(detail -> detail.type() == type).count();
    }
}
