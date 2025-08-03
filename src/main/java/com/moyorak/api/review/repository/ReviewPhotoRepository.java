package com.moyorak.api.review.repository;

import com.moyorak.api.review.domain.ReviewPhoto;
import com.moyorak.api.review.dto.FirstReviewPhotoId;
import com.moyorak.api.review.dto.FirstReviewPhotoPath;
import com.moyorak.api.review.dto.PhotoPath;
import com.moyorak.api.review.dto.ReviewPhotoPath;
import jakarta.persistence.QueryHint;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.data.repository.query.Param;

public interface ReviewPhotoRepository extends JpaRepository<ReviewPhoto, Long> {

    @QueryHints(
            @QueryHint(
                    name = "org.hibernate.comment",
                    value =
                            "ReviewPhotoRepository.findFirstReviewPhotoPathsByIdIn : 리뷰 사진 ID 리스트로 팀 식당별 첫 번째 리뷰 사진 경로 조회"))
    @Query(
            """
                SELECT new com.moyorak.api.review.dto.FirstReviewPhotoPath(r.teamRestaurantId, rp.path)
                FROM ReviewPhoto rp
                JOIN Review r ON rp.reviewId = r.id
                WHERE rp.id IN :reviewPhotoIds AND rp.use = true AND r.use = true
            """)
    List<FirstReviewPhotoPath> findFirstReviewPhotoPathsByIdIn(
            @Param("reviewPhotoIds") List<Long> reviewPhotoIds);

    @QueryHints(
            @QueryHint(
                    name = "org.hibernate.comment",
                    value =
                            "ReviewPhotoRepository.findFirstReviewPhotoIdsByTeamRestaurantIds: 팀 식당 ID 리스트로 팀 식당별 첫 번째 리뷰 사진 ID 조회"))
    @Query(
            """
                    SELECT new com.moyorak.api.review.dto.FirstReviewPhotoId(r.teamRestaurantId, MIN(rp.id))
                    FROM ReviewPhoto rp
                    JOIN Review r ON rp.reviewId = r.id
                    WHERE r.teamRestaurantId IN :teamRestaurantIds AND r.use = true AND rp.use = true
                    GROUP BY r.teamRestaurantId
            """)
    List<FirstReviewPhotoId> findFirstReviewPhotoIdsByTeamRestaurantIds(
            @Param("teamRestaurantIds") List<Long> teamRestaurantIds);

    @QueryHints(
            @QueryHint(
                    name = "org.hibernate.comment",
                    value =
                            "ReviewPhotoRepository.findByReviewPhotosByReviewIds: 리뷰 ID 로 리뷰 사진 path 조회"))
    @Query(
            """
                SELECT new com.moyorak.api.review.dto.ReviewPhotoPath(r.id, rp.path)
                FROM ReviewPhoto rp
                JOIN Review r ON rp.reviewId = r.id
                WHERE r.id IN :reviewIds AND r.use = true AND rp.use = true

            """)
    List<ReviewPhotoPath> findPhotoPathsByReviewIds(List<Long> reviewIds);

    @Query(
            """
        SELECT new com.moyorak.api.review.dto.PhotoPath(rp.path)
        FROM ReviewPhoto rp
        JOIN Review r ON rp.reviewId = r.id
        WHERE r.teamRestaurantId = :teamRestaurantId
          AND r.use = true
          AND rp.use = true
        """)
    @QueryHints(
            @QueryHint(
                    name = "org.hibernate.comment",
                    value =
                            "ReviewPhotoRepository.findByReviewPhotosByReviewIds: 팀 맛집 ID 로 리뷰 사진 path 조회"))
    Page<PhotoPath> findPhotoPathsByTeamRestaurantId(
            @Param("teamRestaurantId") Long teamRestaurantId, Pageable pageable);

    @QueryHints(
            @QueryHint(
                    name = "org.hibernate.comment",
                    value = "ReviewPhotoRepository.findReviewPhotosByReviewId: 리뷰 ID 로 리뷰 사진 조회"))
    List<ReviewPhoto> findReviewPhotosByReviewId(Long reviewId);
}
