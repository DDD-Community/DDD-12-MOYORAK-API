package com.moyorak.infra.orm;

import com.moyorak.api.party.domain.VoteType;
import jakarta.persistence.Converter;

@Converter
public class VoteTypeConverter extends AbstractCommonEnumAttributeConverter<VoteType> {
    public VoteTypeConverter() {
        super(VoteType.class);
    }
}
