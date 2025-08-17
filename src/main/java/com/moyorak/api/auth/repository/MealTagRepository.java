package com.moyorak.api.auth.repository;

import com.moyorak.api.auth.domain.MealTag;
import com.moyorak.api.auth.dto.MealTagTypeCount;
import jakarta.persistence.QueryHint;
import java.util.List;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

public interface MealTagRepository extends CrudRepository<MealTag, Long> {

    @Query(
            """
                SELECT new com.moyorak.api.auth.dto.MealTagTypeCount(f.type, COUNT(f))
                FROM MealTag f
                WHERE f.userId = :userId
                AND f.use = true
                GROUP BY f.type
            """)
    @QueryHints(
            @QueryHint(
                    name = "org.hibernate.comment",
                    value =
                            "FoodFlagRepository.findTypeCountByUserId : 각 타입별 아이템 갯수를 DTO 형식으로 조회합니다."))
    List<MealTagTypeCount> findTypeCountByUserId(@Param("userId") Long userId);

    @QueryHints(
            @QueryHint(
                    name = "org.hibernate.comment",
                    value = "FoodFlagRepository.findByUserIdAndUse : 회원 ID별 등록된 항목을 조회합니다."))
    List<MealTag> findByUserIdAndUse(Long userId, boolean use);

    @Modifying
    @Query(
            """
            UPDATE MealTag mt
            SET mt.userId = null,
                mt.use = false
            WHERE mt.userId = :userId
        """)
    @QueryHints(
            @QueryHint(
                    name = "org.hibernate.comment",
                    value = "FoodFlagRepository.clearByUserId : 회원 ID별 등록된 항목을 조회합니다."))
    void clearByUserId(@Param("userId") Long userId);

    @QueryHints(
            @QueryHint(
                    name = "org.hibernate.comment",
                    value = "FoodFlagRepository.findByUserIdInAndUse : 회원 ID들로 등록된 항목을 조회합니다."))
    List<MealTag> findByUserIdInAndUse(List<Long> userIds, boolean use);
}
