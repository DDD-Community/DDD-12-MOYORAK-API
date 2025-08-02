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
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Comment;

@Getter
@Entity
@Table(name = "food_flags")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MealTag extends AuditInformation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, columnDefinition = "bigint")
    private Long id;

    @NotNull
    @Comment("회원 고유 ID")
    @Column(name = "member_id", nullable = false, columnDefinition = "bigint")
    private Long userId;

    @NotNull
    @Comment("구분")
    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false, columnDefinition = "varchar(16)")
    private MealTagType type;

    @NotNull
    @Comment("항목")
    @Column(name = "item", nullable = false, columnDefinition = "varchar(32)")
    private String item;

    @NotNull
    @Comment("사용 여부")
    @Convert(converter = BooleanYnConverter.class)
    @Column(name = "use_yn", nullable = false, columnDefinition = "char(1)")
    private boolean use = true;

    public static MealTag create(final Long userId, final MealTagType type, final String item) {
        MealTag mealTag = new MealTag();

        mealTag.userId = userId;
        mealTag.type = type;
        mealTag.item = item;

        return mealTag;
    }

    public void delete() {
        this.use = false;
    }
}
