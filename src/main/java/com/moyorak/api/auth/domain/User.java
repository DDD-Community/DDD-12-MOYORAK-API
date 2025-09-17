package com.moyorak.api.auth.domain;

import com.moyorak.infra.orm.AuditInformation;
import com.moyorak.infra.orm.BooleanYnConverter;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Comment;

@Getter
@Entity
@Table(name = "member")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User extends AuditInformation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, columnDefinition = "bigint")
    private Long id;

    @NotNull
    @Comment("이메일")
    @Column(name = "email", nullable = false, columnDefinition = "varchar(64)")
    private String email;

    @NotNull
    @Comment("회원 이름")
    @Column(name = "name", nullable = false, columnDefinition = "varchar(32)")
    private String name;

    @NotNull
    @Comment("성별")
    @Enumerated(value = EnumType.STRING)
    @Column(name = "gender", nullable = false, columnDefinition = "varchar(10)")
    private Gender gender;

    @NotNull
    @Comment("생일")
    @Column(name = "birthday", nullable = false, columnDefinition = "date")
    private LocalDate birthday;

    @Comment("프로필 이미지 URL")
    @Column(name = "profile_image", columnDefinition = "varchar(512)")
    private String profileImage;

    @NotNull
    @Comment("사용 여부")
    @Convert(converter = BooleanYnConverter.class)
    @Column(name = "use_yn", nullable = false, columnDefinition = "char(1)")
    private boolean use = true;

    @Builder(access = AccessLevel.PRIVATE)
    protected User(
            Long id,
            String email,
            String name,
            Gender gender,
            LocalDate birthday,
            String profileImage) {
        this.id = id;
        this.email = email;
        this.name = name;
        this.gender = gender;
        this.birthday = birthday;
        this.profileImage = profileImage;
    }

    public static User registeredUser(
            final String email,
            final String name,
            final Gender gender,
            final LocalDate birthday,
            final String profileImage) {
        return User.builder()
                .email(email)
                .name(name)
                .gender(gender)
                .birthday(birthday)
                .profileImage(profileImage)
                .build();
    }

    /** 회원 탈퇴 */
    public void unregister() {
        this.email = "UNREGISTER";
        this.name = "UNREGISTER";
        this.gender = Gender.UNREGISTER;
        this.birthday = LocalDate.of(9999, 12, 31);
        this.profileImage = null;

        this.use = false;
    }
}
