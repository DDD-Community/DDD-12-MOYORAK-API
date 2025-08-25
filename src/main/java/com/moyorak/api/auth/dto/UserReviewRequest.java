package com.moyorak.api.auth.dto;

import com.moyorak.global.domain.ListRequest;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import org.springdoc.core.annotations.ParameterObject;

@Getter
@ParameterObject
@Schema(title = "사용자 리뷰 조회 요청 DTO")
public class UserReviewRequest extends ListRequest {

    public UserReviewRequest(Integer currentPage, Integer size) {
        super(size, currentPage);
    }
}
