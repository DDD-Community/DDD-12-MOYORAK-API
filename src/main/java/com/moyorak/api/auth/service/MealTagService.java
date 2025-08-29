package com.moyorak.api.auth.service;

import com.moyorak.api.auth.domain.MealTag;
import com.moyorak.api.auth.domain.MealTagGroup;
import com.moyorak.api.auth.domain.MealTagType;
import com.moyorak.api.auth.dto.MealTagResponse;
import com.moyorak.api.auth.dto.MealTagSaveRequest;
import com.moyorak.api.auth.dto.MealTagSearchResponse;
import com.moyorak.api.auth.repository.MealTagRepository;
import com.moyorak.config.exception.BusinessException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumMap;
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
        // 1. DB 기존 태그 조회(활성만)
        final List<MealTag> existingTags = mealTagRepository.findByUserIdAndUse(userId, true);
        final Map<MealTagType, List<MealTag>> existingGrouped =
                existingTags.stream().collect(Collectors.groupingBy(MealTag::getType));

        // 2. 요청 태그 (엔티티 변환)
        final List<MealTag> requestEntities = request.toEntities();
        final Map<MealTagType, List<MealTag>> requestGrouped =
                requestEntities.stream().collect(Collectors.groupingBy(MealTag::getType));

        // 3. 최종적으로 남게 될 태그 개수 계산 (요청 유니크 개수 기준)
        final Map<MealTagType, Long> finalCount = new EnumMap<>(MealTagType.class);
        for (MealTagType type : MealTagType.values()) {
            long reqUnique =
                    requestGrouped.getOrDefault(type, Collections.emptyList()).stream()
                            .map(MealTag::getItem)
                            .distinct()
                            .count();
            finalCount.put(type, reqUnique);
        }

        // 4. 개수 제한 검증
        for (MealTagType type : MealTagType.values()) {
            validateWithinLimit(finalCount.getOrDefault(type, 0L), type);
        }

        final List<MealTag> toInsert = new ArrayList<>();
        final List<MealTag> toSoftDelete = new ArrayList<>();

        // 5. 각 타입별 비교하여 등록/삭제 (순서 보존)
        for (MealTagType type : MealTagType.values()) {
            final List<MealTag> requestedList =
                    requestGrouped.getOrDefault(type, Collections.emptyList());
            final List<MealTag> existingList =
                    existingGrouped.getOrDefault(type, Collections.emptyList());

            // 존재 여부 판정용 집합
            final Set<String> existingItems =
                    existingList.stream().map(MealTag::getItem).collect(Collectors.toSet());

            final Set<String> requestedItems =
                    requestedList.stream().map(MealTag::getItem).collect(Collectors.toSet());

            // 등록: "요청 리스트 순서" 그대로 진행 + 중복 요청 방지
            final Set<String> seenInsert = new HashSet<>();
            for (MealTag req : requestedList) {
                final String it = req.getItem();
                if (seenInsert.add(it) && !existingItems.contains(it)) {
                    toInsert.add(req); // 입력 순서 보존
                }
            }

            // 소프트 삭제: "기존 리스트 순서" 그대로 진행
            for (MealTag exist : existingList) {
                if (!requestedItems.contains(exist.getItem())) {
                    exist.delete(); // use=false
                    toSoftDelete.add(exist); // 기존 순서 보존
                }
            }
        }

        // 요청에 없는 타입 → 전부 비활성화
        for (Map.Entry<MealTagType, List<MealTag>> entry : existingGrouped.entrySet()) {
            if (!requestGrouped.containsKey(entry.getKey())) {
                for (MealTag exist : entry.getValue()) {
                    if (exist.isUse()) {
                        exist.delete();
                        toSoftDelete.add(exist);
                    }
                }
            }
        }

        // 6. 저장 및 soft delete
        if (!toInsert.isEmpty()) {
            mealTagRepository.saveAll(toInsert);
        }
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

    private void validateWithinLimit(long finalCount, MealTagType type) {
        if (finalCount > MAX_ITEMS_PER_TYPE) {
            throw new BusinessException(
                    String.format(
                            "%s 타입은 최대 %d개까지만 등록 가능합니다.",
                            type.getDescription(), MAX_ITEMS_PER_TYPE));
        }
    }

    /**
     * 회원의 알러지 + 비선호 음식 정보 조회
     *
     * @param userId 회원 고유 ID
     * @return
     */
    @Transactional(readOnly = true)
    public MealTagSearchResponse getMealTags(final Long userId) {
        final List<MealTag> mealTags = mealTagRepository.findByUserIdAndUse(userId, true);

        final MealTagGroup group = MealTagGroup.from(mealTags);

        return MealTagSearchResponse.from(group);
    }

    @Transactional(readOnly = true)
    public Map<Long, MealTagResponse> getMealTags(final List<Long> userIds) {
        final List<MealTag> existing = mealTagRepository.findByUserIdInAndUse(userIds, true);

        Map<Long, Map<MealTagType, List<String>>> grouped =
                existing.stream()
                        .collect(
                                Collectors.groupingBy(
                                        MealTag::getUserId,
                                        Collectors.groupingBy(
                                                MealTag::getType,
                                                () -> new EnumMap<>(MealTagType.class),
                                                Collectors.mapping(
                                                        MealTag::getItem,
                                                        Collectors.collectingAndThen(
                                                                Collectors.toCollection(
                                                                        LinkedHashSet::new),
                                                                ArrayList::new)))));

        return grouped.entrySet().stream()
                .collect(
                        Collectors.toMap(Map.Entry::getKey, e -> MealTagResponse.of(e.getValue())));
    }
}
