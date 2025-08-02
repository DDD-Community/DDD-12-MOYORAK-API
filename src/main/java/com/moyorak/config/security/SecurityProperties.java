package com.moyorak.config.security;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Getter
@Configuration
class SecurityProperties {

    private final String redirectUrl;

    public SecurityProperties(@Value("${security.redirect-url}") final String redirectUrl) {
        this.redirectUrl = redirectUrl;
    }
}
