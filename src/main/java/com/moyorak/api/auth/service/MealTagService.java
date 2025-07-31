package com.moyorak.api.auth.service;

import com.moyorak.api.auth.domain.MealTag;
import com.moyorak.api.auth.domain.MealTagSummary;
import com.moyorak.api.auth.domain.MealTagType;
import com.moyorak.api.auth.dto.MealTagSaveRequest;
import com.moyorak.api.auth.repository.MealTagRepository;
import com.moyorak.config.exception.BusinessException;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MealTagService {

    private final MealTagRepository mealTagRepository;

    @Transactional
    public void foodFlagRegister(final Long userId, final MealTagSaveRequest request) {
        // 1. 저장 된 항목 갯수와, 저장하고자 하는 갯수가 최대 값을 초과하는지 확인
        final MealTagSummary count =
                MealTagSummary.create(mealTagRepository.findTypeCountByUserId(userId));

        for (MealTagType type : MealTagType.values()) {
            validateWithinLimit(count, request, type);
        }

        // 2. 기존 저장된 항목과, 신규 등록 항목이 중복되는 것은 제외
        final List<MealTag> existingTags = mealTagRepository.findByUserIdAndUse(userId, true);
        final Set<MealTagType> existingTypes =
                existingTags.stream().map(MealTag::getType).collect(Collectors.toSet());

        final List<MealTag> newEntities =
                request.toEntities().stream()
                        .filter(mealTag -> !existingTypes.contains(mealTag.getType()))
                        .toList();

        // 3. 저장
        mealTagRepository.saveAll(newEntities);
    }

    /**
     * 회원의 알러지 + 비선호 음식 정보 초기화 <br>
     * <br>
     * 회원 탈퇴 시, 회원의 식사 태그 정보 미사용 처리합니다.
     *
     * @param userId 회원 고유 ID
     */
    @Transactional
    public void clear(final Long userId) {
        mealTagRepository.clearByUserId(userId);
    }

    private void validateWithinLimit(
            MealTagSummary count, MealTagSaveRequest request, MealTagType type) {
        if (!count.isWithinLimit(type, request.getCountByType(type))) {
            throw new BusinessException(
                    String.format(
                            "%s 타입은 최대 %d개까지만 등록 가능합니다.",
                            type.getDescription(), MealTagSummary.MAX_ITEMS_PER_TYPE));
        }
    }
}
