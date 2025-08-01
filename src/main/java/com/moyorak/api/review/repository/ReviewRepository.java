package com.moyorak.api.review.repository;

import com.moyorak.api.review.domain.Review;
import com.moyorak.api.review.dto.ReviewWithUserProjection;
import jakarta.persistence.QueryHint;
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
        r.id, r.extraText,r.score, r.servingTime, r.waitingTime,
        u.name, u.profileImage,r.createdDate
    )
    FROM Review r
    JOIN User u ON r.userId = u.id
    WHERE r.teamRestaurantId = :teamRestaurantId AND r.use = true
""")
    Page<ReviewWithUserProjection> findReviewWithUserByTeamRestaurantId(
            @Param("teamRestaurantId") Long teamRestaurantId, Pageable pageable);
}
