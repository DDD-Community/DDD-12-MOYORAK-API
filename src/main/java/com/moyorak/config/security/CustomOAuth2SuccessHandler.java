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
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
class CustomOAuth2SuccessHandler implements AuthenticationSuccessHandler {

    private final AuthService authService;
    private final ObjectMapper objectMapper;

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

            response.setStatus(HttpServletResponse.SC_OK);
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            response.setCharacterEncoding(StandardCharsets.UTF_8.name());
            response.getWriter().write(objectMapper.writeValueAsString(oAuthResponse));

            return;
        }

        final SignInResponse signInResponse = authService.generate(userPrincipal.getId());

        response.setStatus(HttpServletResponse.SC_OK);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        response.getWriter().write(objectMapper.writeValueAsString(signInResponse));
    }
}
