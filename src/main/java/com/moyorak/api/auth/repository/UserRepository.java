package com.moyorak.api.auth.repository;

import com.moyorak.api.auth.domain.User;
import jakarta.persistence.QueryHint;
import java.util.Optional;
import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.data.repository.CrudRepository;

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
}
