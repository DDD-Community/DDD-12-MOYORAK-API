package com.moyorak.api.auth.service;

import com.moyorak.api.auth.domain.InvalidTokenException;
import com.moyorak.api.auth.domain.User;
import com.moyorak.api.auth.domain.UserNotFoundException;
import com.moyorak.api.auth.domain.UserPrincipal;
import com.moyorak.api.auth.domain.UserToken;
import com.moyorak.api.auth.dto.RefreshResponse;
import com.moyorak.api.auth.dto.SignInResponse;
import com.moyorak.api.auth.repository.TokenRepository;
import com.moyorak.api.auth.repository.UserRepository;
import com.moyorak.config.exception.BusinessException;
import com.moyorak.config.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final TokenRepository tokenRepository;

    private final UserRepository userRepository;

    private final JwtTokenProvider jwtTokenProvider;

    @Transactional
    public SignInResponse generate(final Long userId) {
        final User user = userRepository.findById(userId).orElseThrow(UserNotFoundException::new);

        final UserPrincipal userPrincipal =
                UserPrincipal.generate(userId, user.getEmail(), user.getName());

        final String token = jwtTokenProvider.generateAccessToken(userPrincipal);
        final String refreshToken = jwtTokenProvider.generateRefreshToken(userPrincipal);

        tokenRepository.save(UserToken.create(userId, token, refreshToken));

        return new SignInResponse(token, refreshToken);
    }

    /**
     * 로그아웃을 요청합니다. <br>
     * 토큰 정보를 초기화합니다.
     *
     * @param userId 회원 고유 ID
     */
    @Transactional
    public void signOut(final Long userId) {
        UserToken userToken = getUserLastToken(userId);

        if (userToken.isInValidToken()) {
            throw new BusinessException("로그인 정보가 존재하지 않습니다.");
        }

        userToken.clear();
    }

    @Transactional(readOnly = true)
    public void validToken(final Long userId, final String token) {
        final UserToken userToken = getUserLastToken(userId);

        if (!userToken.isEqualsToken(token)) {
            throw new InvalidTokenException();
        }
    }

    @Transactional
    public RefreshResponse refresh(final String refreshToken) {
        // TODO: 분리할 수 있으면 분리하기
        if (!StringUtils.hasText(refreshToken)) {
            throw new InvalidTokenException();
        }

        // 1. Refersh Token이 유효한지 확인
        if (!jwtTokenProvider.isValidRefreshToken(refreshToken)) {
            // TODO: exception handler 추가하기
            throw new InvalidTokenException();
        }

        // 2. 회원이 유효한지 확인
        final Long userId = jwtTokenProvider.getUserIdByRefreshToken(refreshToken);

        final User user = userRepository.findById(userId).orElseThrow(UserNotFoundException::new);

        final UserToken userToken =
                tokenRepository
                        .findFirstByUserIdOrderByIdDesc(userId)
                        .orElseThrow(() -> new BusinessException("로그인 정보가 존재하지 않습니다."));

        // 3. 저장 된 Refresh Token이 있는지 확인
        if (!userToken.isEqualsRefreshToken(refreshToken)) {
            throw new InvalidTokenException();
        }

        // 4. 재발급
        final UserPrincipal userPrincipal =
                UserPrincipal.generate(userId, user.getEmail(), user.getName());
        final String newToken = jwtTokenProvider.generateAccessToken(userPrincipal);
        final String newRefreshToken = jwtTokenProvider.generateRefreshToken(userPrincipal);

        // 5. 업데이트
        userToken.refresh(newToken, newRefreshToken);

        return RefreshResponse.create(newToken, newRefreshToken);
    }

    private UserToken getUserLastToken(final Long userId) {
        userRepository.findById(userId).orElseThrow(UserNotFoundException::new);

        return tokenRepository
                .findFirstByUserIdOrderByIdDesc(userId)
                .orElseThrow(() -> new BusinessException("로그인 정보가 존재하지 않습니다."));
    }
}
