package com.moyorak.api.review.repository;

import com.moyorak.api.review.domain.ReviewServingTime;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReviewServingTimeRepository extends JpaRepository<ReviewServingTime, Long> {
    List<ReviewServingTime> findAllByUse(boolean use);
}
