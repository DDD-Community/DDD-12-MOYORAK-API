package com.moyorak.api.auth.service;

import com.moyorak.api.auth.domain.User;
import com.moyorak.api.auth.dto.SignUpRequest;
import com.moyorak.api.auth.dto.SignUpResponse;
import com.moyorak.api.auth.repository.UserRepository;
import com.moyorak.config.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    @Transactional
    public SignUpResponse signUp(final SignUpRequest request) {
        // 등록된 이메일인지 확인
        final boolean isRegistered =
                userRepository.findByEmailAndUse(request.email(), true).isPresent();

        if (isRegistered) {
            throw new BusinessException("중복된 이메일입니다.");
        }

        final User user = request.toEntity();

        return SignUpResponse.create(userRepository.save(user).getId());
    }
}
