package com.moyorak.api.company.domain;

import org.springframework.test.util.ReflectionTestUtils;

public class CompanySearchFixture {
    public static CompanySearch fixture(
            final Long id,
            final Long companyId,
            final String name,
            final double longitude,
            final double latitude,
            final boolean use) {
        CompanySearch companySearch = new CompanySearch();

        ReflectionTestUtils.setField(companySearch, "id", id);
        ReflectionTestUtils.setField(companySearch, "companyId", companyId);
        ReflectionTestUtils.setField(companySearch, "name", name);
        ReflectionTestUtils.setField(companySearch, "longitude", longitude);
        ReflectionTestUtils.setField(companySearch, "latitude", latitude);
        ReflectionTestUtils.setField(companySearch, "use", use);

        return companySearch;
    }
}
