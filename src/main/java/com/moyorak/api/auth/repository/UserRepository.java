package com.moyorak.api.auth.repository;

import com.moyorak.api.auth.domain.User;
import com.moyorak.api.auth.dto.UserDailyStatesResponse;
import jakarta.persistence.QueryHint;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

public interface UserRepository extends CrudRepository<User, Long> {

    @QueryHints(
            @QueryHint(
                    name = "org.hibernate.comment",
                    value = "UserRepository.findByEmail : 이메일로 회원 정보를 조회합니다."))
    Optional<User> findByEmailAndUse(String email, Boolean isUse);

    @QueryHints(
            @QueryHint(
                    name = "org.hibernate.comment",
                    value = "UserRepository.findByIdAndUse : 회원 고유 ID와 활성 여부로 회원 정보를 조회합니다."))
    Optional<User> findByIdAndUse(Long id, Boolean isUse);

    @Query(
            """
        SELECT new com.moyorak.api.auth.dto.UserDailyStatesResponse(u.id, u.name, uds.state)
        FROM User u
        LEFT JOIN UserDailyState uds ON uds.userId = u.id AND uds.recordDate = :today
        WHERE u.use = true
        """)
    @QueryHints(
            @QueryHint(
                    name = "org.hibernate.comment",
                    value = "UserRepository.findByUsersWithDailyState : 혼밥 여부가 포함된 회원 정보를 조회합니다."))
    List<UserDailyStatesResponse> findByUsersWithDailyState(@Param("today") LocalDate today);
}
