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
import jakarta.persistence.UniqueConstraint;
import java.math.BigDecimal;
import java.math.RoundingMode;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Comment;

@Getter
@Entity
@Table(
        name = "company",
        uniqueConstraints = {
            @UniqueConstraint(
                    name = "uk_company_name_location",
                    columnNames = {"name", "rounded_longitude", "rounded_latitude"})
        })
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Company extends AuditInformation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, columnDefinition = "bigint")
    private Long id;

    @Comment("회사 이름")
    @Column(name = "name", nullable = false, columnDefinition = "varchar(64)")
    private String name;

    @Comment("회사 주소")
    @Column(name = "address", nullable = false)
    private String address;

    @Comment("회사 상세 주소")
    @Column(name = "address_detail")
    private String addressDetail;

    @Comment("회사 위치 경도")
    @Column(name = "longitude", nullable = false, columnDefinition = "double")
    private double longitude;

    @Comment("회사 위치 위도")
    @Column(name = "latitude", nullable = false, columnDefinition = "double")
    private double latitude;

    @Comment("사용 여부")
    @Convert(converter = BooleanYnConverter.class)
    @Column(name = "use_yn", nullable = false, columnDefinition = "char(1)")
    private boolean use = true;

    @Column(name = "rounded_longitude", precision = 8, scale = 5, nullable = false)
    private BigDecimal roundedLongitude;

    @Column(name = "rounded_latitude", precision = 8, scale = 5, nullable = false)
    private BigDecimal roundedLatitude;

    private static final int SCALE = 5;

    @Builder(access = AccessLevel.PRIVATE)
    private Company(
            String name, String address, String addressDetail, double longitude, double latitude) {
        this.name = name;
        this.address = address;
        this.addressDetail = addressDetail;
        this.longitude = longitude;
        this.latitude = latitude;
        roundedLongitude = roundToScale(longitude);
        roundedLatitude = roundToScale(latitude);
    }

    public static Company create(
            String name, String address, String addressDetail, double longitude, double latitude) {
        return Company.builder()
                .name(name)
                .address(address)
                .addressDetail(addressDetail)
                .longitude(longitude)
                .latitude(latitude)
                .build();
    }

    private BigDecimal roundToScale(final double value) {
        return BigDecimal.valueOf(value).setScale(SCALE, RoundingMode.HALF_UP);
    }
}
