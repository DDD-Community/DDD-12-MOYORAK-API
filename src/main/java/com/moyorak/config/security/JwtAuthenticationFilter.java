package com.moyorak.config.security;

import com.moyorak.api.auth.domain.UserPrincipal;
import com.moyorak.api.auth.service.AuthService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.util.ObjectUtils;
import org.springframework.web.filter.OncePerRequestFilter;

@RequiredArgsConstructor
class JwtAuthenticationFilter extends OncePerRequestFilter {

    public static final String JWT_TOKEN_PREFIX = "Bearer ";

    private final JwtTokenProvider jwtTokenProvider;
    private final AuthenticationEntryPoint authenticationEntryPoint;

    private final AuthService authService;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        final String authorization = request.getHeader(HttpHeaders.AUTHORIZATION);

        try {
            if (ObjectUtils.isEmpty(authorization)) {
                throw new AuthenticationCredentialsNotFoundException("로그인 정보가 없습니다.");
            }

            if (!authorization.startsWith(JWT_TOKEN_PREFIX)) {
                throw new BadCredentialsException("잘못된 요청입니다.");
            }

            final String token = authorization.substring(JWT_TOKEN_PREFIX.length());

            if (!jwtTokenProvider.isValidToken(token)) {
                throw new BadCredentialsException("잘못된 요청입니다.");
            }

            final Authentication authentication = jwtTokenProvider.getAuthentication(token);

            validToken(authentication, token);

            SecurityContextHolder.getContext().setAuthentication(authentication);

            filterChain.doFilter(request, response);
        } catch (AuthenticationException e) {
            authenticationEntryPoint.commence(request, response, e);
        }
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        final String uri = request.getRequestURI();

        return uri.startsWith("/api/auth/sign-in")
                || uri.startsWith("/api/auth/refresh")
                || uri.startsWith("/api/user/sign-up");
    }

    private void validToken(final Authentication authentication, final String token) {
        final UserPrincipal principal = (UserPrincipal) authentication.getPrincipal();
        authService.validToken(principal.getId(), token);
    }
}
