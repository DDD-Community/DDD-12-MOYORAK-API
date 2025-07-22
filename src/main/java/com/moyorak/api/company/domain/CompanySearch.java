package com.moyorak.api.company.domain;

import com.moyorak.infra.orm.AuditInformation;
import com.moyorak.infra.orm.BooleanYnConverter;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Comment;

@Getter
@Entity
@Table(name = "company_search")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CompanySearch extends AuditInformation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, columnDefinition = "bigint")
    private Long id;

    @NotNull
    @Comment("회사 고유 ID")
    @Column(name = "company_id", nullable = false)
    private Long companyId;

    @Comment("회사 이름")
    @Column(name = "name", nullable = false, columnDefinition = "varchar(64)")
    private String name;

    @Comment("사용 여부")
    @Convert(converter = BooleanYnConverter.class)
    @Column(name = "use_yn", nullable = false, columnDefinition = "char(1)")
    private boolean use = true;

    @Builder(access = AccessLevel.PRIVATE)
    private CompanySearch(Long companyId, String name) {
        this.companyId = companyId;
        this.name = name;
    }

    public static CompanySearch create(final Long companyId, final String name) {
        return CompanySearch.builder().companyId(companyId).name(name).build();
    }
}
