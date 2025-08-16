package com.moyorak.api.auth.service;

import com.moyorak.api.auth.domain.User;
import com.moyorak.api.auth.domain.UserNotFoundException;
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

    /**
     * 회원 탈퇴 <br>
     * <br>
     * 회원 탈퇴 시, 회원의 이름, 생년월일, 성별, 알러지 + 비선호 음식, 회사, 팀 정보가 제거됩니다.
     *
     * @param id 회원 ID
     */
    @Transactional
    public void unregister(final Long id) {
        // 1. 회원 정보가 있는지 조회
        User user = userRepository.findByIdAndUse(id, true).orElseThrow(UserNotFoundException::new);

        // 2. 사용하지 않는 정보 clear
        user.unregister();
    }
}
