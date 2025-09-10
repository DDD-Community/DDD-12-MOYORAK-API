package com.moyorak.api.review.repository;

import com.moyorak.api.auth.dto.ReviewWithUserAndTeamRestaurantProjection;
import com.moyorak.api.review.domain.Review;
import com.moyorak.api.review.dto.ReviewWithUserProjection;
import jakarta.persistence.QueryHint;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.data.repository.query.Param;

public interface ReviewRepository extends JpaRepository<Review, Long> {
    @QueryHints(
            @QueryHint(
                    name = "org.hibernate.comment",
                    value =
                            "ReviewRepository.findReviewWithUserByTeamRestaurantId: 팀 맛집 ID 로 리뷰와 사용자 정보 조회 "))
    @Query(
            """
    SELECT new com.moyorak.api.review.dto.ReviewWithUserProjection(
        r.id, r.extraText,r.score, r.servingTime, r.waitingTime,u.id,
        u.name, u.profileImage,r.createdDate
    )
    FROM Review r
    JOIN User u ON r.userId = u.id
    WHERE r.teamRestaurantId = :teamRestaurantId AND r.use = true
""")
    Page<ReviewWithUserProjection> findReviewWithUserByTeamRestaurantId(
            @Param("teamRestaurantId") Long teamRestaurantId, Pageable pageable);

    @QueryHints(
            @QueryHint(
                    name = "org.hibernate.comment",
                    value =
                            "ReviewRepository.findReviewWithUserAndTeamRestaurantByUserId: 사용자 ID 로 리뷰와 사용자 정보,맛집 정보 조회 "))
    @Query(
            """
SELECT new com.moyorak.api.auth.dto.ReviewWithUserAndTeamRestaurantProjection(
r.id,rt.name,t.id,t.use, r.extraText,r.score, r.servingTime, r.waitingTime,u.id,
u.name, u.profileImage,r.createdDate
)
FROM Review r
JOIN User u ON r.userId = u.id
JOIN TeamRestaurant t ON t.id = r.teamRestaurantId
JOIN Restaurant rt ON rt.id = t.restaurant.id
WHERE r.userId = :userId AND r.use = true
""")
    Page<ReviewWithUserAndTeamRestaurantProjection> findReviewWithUserAndTeamRestaurantByUserId(
            @Param("userId") Long userId, Pageable pageable);

    @QueryHints(
            @QueryHint(
                    name = "org.hibernate.comment",
                    value = "ReviewRepository.findByIdAndUseIsTrue: ID 로 리뷰 조회"))
    Optional<Review> findByIdAndUseIsTrue(Long id);
}
