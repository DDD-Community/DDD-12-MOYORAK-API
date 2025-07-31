package com.moyorak.api.review.repository;

import com.moyorak.api.review.domain.ReviewWaitingTime;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReviewWaitingTimeRepository extends JpaRepository<ReviewWaitingTime, Long> {
    List<ReviewWaitingTime> findAllByUse(boolean use);

    Optional<ReviewWaitingTime> findByIdAndUseIsTrue(Long id);
}
