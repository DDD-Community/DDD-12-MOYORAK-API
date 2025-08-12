package com.moyorak.api.party.dto;

import com.moyorak.global.domain.ListRequest;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import org.springdoc.core.annotations.ParameterObject;

@Getter
@ParameterObject
@Schema(title = "파티 목록 조회 요청 DTO")
public class PartyListRequest extends ListRequest {
    public PartyListRequest(Integer currentPage, Integer size) {
        super(size, currentPage);
    }
}
