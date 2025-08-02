package com.moyorak.config.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.moyorak.api.auth.domain.UserPrincipal;
import com.moyorak.api.auth.dto.OAuthResponse;
import com.moyorak.api.auth.dto.SignInResponse;
import com.moyorak.api.auth.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

@Component
@RequiredArgsConstructor
class CustomOAuth2SuccessHandler implements AuthenticationSuccessHandler {

    private final AuthService authService;
    private final ObjectMapper objectMapper;

    private final SecurityProperties securityProperties;

    @Override
    public void onAuthenticationSuccess(
            HttpServletRequest request, HttpServletResponse response, Authentication authentication)
            throws IOException {

        final UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();

        final Boolean isNew = userPrincipal.getAttribute("isNew");

        if (isNew) {
            final String profileImage = userPrincipal.getAttribute("picture");

            final OAuthResponse oAuthResponse =
                    OAuthResponse.create(
                            userPrincipal.getUsername(), userPrincipal.getName(), profileImage);

            final String encodedName =
                    Base64.getUrlEncoder()
                            .withoutPadding()
                            .encodeToString(oAuthResponse.name().getBytes(StandardCharsets.UTF_8));

            final String targetUrl =
                    UriComponentsBuilder.fromUriString(securityProperties.getRedirectUrl())
                            .queryParam("email", oAuthResponse.email())
                            .queryParam("name", encodedName)
                            .queryParam("profileImage", oAuthResponse.profileImage())
                            .build()
                            .toUriString();

            response.sendRedirect(targetUrl);

            return;
        }

        final SignInResponse signInResponse = authService.generate(userPrincipal.getId());

        final String targetUrl =
                UriComponentsBuilder.fromUriString(securityProperties.getRedirectUrl())
                        .queryParam("accessToken", signInResponse.accessToken())
                        .queryParam("refreshToken", signInResponse.refreshToken())
                        .build()
                        .toUriString();

        response.sendRedirect(targetUrl);
    }
}
