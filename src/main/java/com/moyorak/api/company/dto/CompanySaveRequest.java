package com.moyorak.api.company.dto;

import com.moyorak.api.company.domain.Company;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Schema(title = "[회사] 모여락 DB에 회사 저장 DTO")
public record CompanySaveRequest(
        @NotBlank(message = "회사 이름을 입력해주세요.")
                @Size(max = 255, message = "회사 이름은 {max}자 이하여야 합니다.")
                @Schema(description = "회사 이름", example = "우가우가 차차차")
                String name,
        @NotBlank(message = "회사 주소를 입력해주세요.")
                @Size(max = 255, message = "회사 주소는 {max}자 이하여야 합니다.")
                @Schema(description = "회사 상세 주소", example = "우가우가시 차차차동 24번길")
                String address,
        @Size(max = 255, message = "회사 상세 주소는 {max}자 이하여야 합니다.")
                @Schema(description = "회사 상세 주소", example = "우가우가동 차차차호")
                String addressDetail,
        @NotNull(message = "경도를 입력해주세요.")
                @DecimalMin(value = "-180.0", message = "경도는 {value} 이상이어야 합니다.")
                @DecimalMax(value = "180.0", message = "경도는 {value} 이하여야 합니다.")
                @Schema(description = "경도", example = "127.043616")
                Double longitude,
        @NotNull(message = "위도를 입력해주세요.")
                @DecimalMin(value = "-90.0", message = "위도는 {value} 이상이어야 합니다.")
                @DecimalMax(value = "90.0", message = "위도는 {value} 이하여야 합니다.")
                @Schema(description = "위도", example = "37.503095")
                Double latitude) {
    public Company toCompany() {
        return Company.create(name, address, addressDetail, longitude, latitude);
    }
}
