package com.moyorak.api.review.domain;

import com.moyorak.infra.orm.AuditInformation;
import com.moyorak.infra.orm.BooleanYnConverter;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Comment;

@Getter
@Entity
@Table(name = "review_photo")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ReviewPhoto extends AuditInformation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Comment("이미지 path")
    @Column(name = "path", nullable = false, columnDefinition = "varchar(128)")
    private String path;

    @Comment("사용 여부")
    @Convert(converter = BooleanYnConverter.class)
    @Column(name = "use_yn", nullable = false, columnDefinition = "char(1)")
    private boolean use = true;

    @Comment("리뷰 고유 ID")
    @Column(name = "review_id", nullable = false)
    private Long reviewId;

    public void toggleUse() {
        this.use = !this.use;
    }

    public static ReviewPhoto create(final String photoPath, final Long reviewId) {
        ReviewPhoto photo = new ReviewPhoto();
        photo.path = photoPath;
        photo.reviewId = reviewId;
        return photo;
    }
}
