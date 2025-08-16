package com.moyorak.api.auth.service;

import com.moyorak.api.auth.domain.MealTag;
import com.moyorak.api.auth.domain.MealTagType;
import com.moyorak.api.auth.dto.MealTagSaveRequest;
import com.moyorak.api.auth.repository.MealTagRepository;
import com.moyorak.config.exception.BusinessException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MealTagService {

    public static final long MAX_ITEMS_PER_TYPE = 10L;

    private final MealTagRepository mealTagRepository;

    @Transactional
    public void foodFlagRegister(final Long userId, final MealTagSaveRequest request) {
        // 1. DB 기존 태그 조회
        final List<MealTag> existingTags = mealTagRepository.findByUserIdAndUse(userId, true);
        final Map<MealTagType, List<MealTag>> existingGrouped =
                existingTags.stream().collect(Collectors.groupingBy(MealTag::getType));

        // 2. 요청 태그
        final List<MealTag> requestEntities = request.toEntities();
        final Map<MealTagType, List<MealTag>> requestGrouped =
                requestEntities.stream().collect(Collectors.groupingBy(MealTag::getType));

        // 3. 최종적으로 남게 될 태그 개수 계산
        // 기존 개수 + 신규 개수 - 삭제될 개수
        Map<MealTagType, Long> finalCount = new HashMap<>();

        for (MealTagType type : MealTagType.values()) {
            // DB에서 해당 타입 값 (중복 제거)
            Set<String> existingSet =
                    existingGrouped.getOrDefault(type, Collections.emptyList()).stream()
                            .map(MealTag::getItem) // 중복 판단 키
                            .collect(Collectors.toSet());

            // 요청에서 해당 타입 값 (중복 제거)
            Set<String> requestSet =
                    requestGrouped.getOrDefault(type, Collections.emptyList()).stream()
                            .map(MealTag::getItem)
                            .collect(Collectors.toSet());

            // 최종 유니크 합집합
            Set<String> finalUnique = new HashSet<>(existingSet);
            finalUnique.addAll(requestSet);

            finalCount.put(type, (long) finalUnique.size());
        }

        // 4. 개수 제한 검증
        for (MealTagType type : MealTagType.values()) {
            validateWithinLimit(finalCount.get(type), type);
        }

        final List<MealTag> toInsert = new ArrayList<>();
        final List<MealTag> toSoftDelete = new ArrayList<>();

        // 5. 각 타입별 비교하여 등록/삭제
        for (Map.Entry<MealTagType, List<MealTag>> entry : requestGrouped.entrySet()) {
            MealTagType type = entry.getKey();
            List<MealTag> requestedList = entry.getValue();
            List<MealTag> existingList =
                    existingGrouped.getOrDefault(type, Collections.emptyList());

            // 등록 (요청 개수가 더 많으면 초과분 삽입)
            if (requestedList.size() > existingList.size()) {
                toInsert.addAll(requestedList.subList(existingList.size(), requestedList.size()));
            }

            // 소프트 삭제 (기존 개수가 더 많으면 초과분 비활성화)
            if (existingList.size() > requestedList.size()) {
                toSoftDelete.addAll(
                        existingList.subList(requestedList.size(), existingList.size()));
            }
        }

        // 요청에 없는 타입 → 전부 비활성화
        for (Map.Entry<MealTagType, List<MealTag>> entry : existingGrouped.entrySet()) {
            if (!requestGrouped.containsKey(entry.getKey())) {
                toSoftDelete.addAll(entry.getValue());
            }
        }

        // 6. 저장 및 soft delete
        mealTagRepository.saveAll(toInsert);
        toSoftDelete.forEach(MealTag::delete);
    }

    private void validateWithinLimit(long finalCount, MealTagType type) {
        if (finalCount > MAX_ITEMS_PER_TYPE) {
            throw new BusinessException(
                    String.format(
                            "%s 타입은 최대 %d개까지만 등록 가능합니다.",
                            type.getDescription(), MAX_ITEMS_PER_TYPE));
        }
    }

    @Transactional(readOnly = true)
    public Map<Long, Map<MealTagType, List<String>>> getMealTags(final List<Long> userIds) {
        final List<MealTag> existingTags = mealTagRepository.findByUserIdInAndUse(userIds, true);

        return existingTags.stream()
                .collect(
                        Collectors.groupingBy(
                                MealTag::getUserId,
                                Collectors.groupingBy(
                                        MealTag::getType,
                                        Collectors.collectingAndThen(
                                                Collectors.mapping(
                                                        MealTag::getItem,
                                                        Collectors.toCollection(
                                                                LinkedHashSet::new)),
                                                ArrayList::new))));
    }
}
