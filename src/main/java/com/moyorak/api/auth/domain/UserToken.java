package com.moyorak.api.auth.domain;

import com.moyorak.infra.orm.AuditInformation;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Comment;
import org.springframework.util.StringUtils;

@Getter
@Entity
@Table(name = "member_login_token")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserToken extends AuditInformation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, columnDefinition = "bigint")
    private Long id;

    @Comment("회원 고유 ID")
    @Column(name = "member_id", nullable = false, columnDefinition = "bigint")
    private Long userId;

    @Comment("액세스 토큰")
    @Column(name = "access_token", columnDefinition = "varchar(256)")
    private String accessToken;

    @Comment("리프레시 토큰")
    @Column(name = "refresh_token", columnDefinition = "varchar(256)")
    private String refreshToken;

    public static UserToken create(
            final Long userId, final String accessToken, final String refreshToken) {
        UserToken userToken = new UserToken();

        userToken.userId = userId;
        userToken.accessToken = accessToken;
        userToken.refreshToken = refreshToken;

        return userToken;
    }

    public boolean isInValidToken() {
        return !StringUtils.hasText(this.accessToken);
    }

    /**
     * 등록 된 토큰을 초기화합니다. <br>
     * 로그아웃 혹은 탈퇴때 사용됩니다.
     */
    public void clear() {
        this.accessToken = null;
        this.refreshToken = null;
    }

    public boolean isEqualsToken(final String token) {
        if (!StringUtils.hasText(token)) {
            return false;
        }

        return token.equals(this.accessToken);
    }

    public boolean isEqualsRefreshToken(final String refreshToken) {
        if (!StringUtils.hasText(refreshToken)) {
            return false;
        }

        return refreshToken.equals(this.refreshToken);
    }

    public void refresh(final String accessToken, final String refreshToken) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
    }
}
