package com.moyorak.infra.orm;

import com.moyorak.api.party.domain.VoteStatus;
import jakarta.persistence.Converter;

@Converter
public class VoteStatusConverter extends AbstractCommonEnumAttributeConverter<VoteStatus> {
    public VoteStatusConverter() {
        super(VoteStatus.class);
    }
}
