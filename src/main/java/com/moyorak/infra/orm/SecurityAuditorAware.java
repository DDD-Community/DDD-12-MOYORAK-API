package com.moyorak.infra.orm;

import com.moyorak.api.auth.domain.UserPrincipal;
import java.util.Optional;
import org.springframework.data.domain.AuditorAware;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

@Component
class SecurityAuditorAware implements AuditorAware<Long> {

    @Override
    public Optional<Long> getCurrentAuditor() {
        final Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        if (ObjectUtils.isEmpty(authentication) || !authentication.isAuthenticated()) {
            return Optional.of(0L);
        }

        if (authentication instanceof AnonymousAuthenticationToken) {
            return Optional.of(0L);
        }

        final UserPrincipal principal = (UserPrincipal) authentication.getPrincipal();

        if (!ObjectUtils.isEmpty(principal) && principal.getId() != null) {
            return Optional.of(principal.getId());
        }

        return Optional.of(0L);
    }
}
