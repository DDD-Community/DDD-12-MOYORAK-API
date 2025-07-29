package com.moyorak.api.team.domain;

import com.moyorak.api.company.domain.Company;
import org.springframework.test.util.ReflectionTestUtils;

public class TeamFixture {

    public static Team fixture(final Long id, final Company company, final boolean ues) {
        Team team = new Team();

        ReflectionTestUtils.setField(team, "id", id);
        ReflectionTestUtils.setField(team, "company", company);
        ReflectionTestUtils.setField(team, "use", ues);

        return team;
    }

    public static Team fixture(final Long id, final boolean ues) {
        Team team = new Team();

        ReflectionTestUtils.setField(team, "id", id);
        ReflectionTestUtils.setField(team, "use", ues);

        return team;
    }
}
