package com.moyorak.api.team.repository;

import com.moyorak.api.company.domain.Company;
import com.moyorak.api.team.domain.Team;
import org.springframework.data.repository.CrudRepository;

public interface TeamRepository extends CrudRepository<Team, Long> {

    boolean existsByCompanyAndName(Company company, String name);
}
