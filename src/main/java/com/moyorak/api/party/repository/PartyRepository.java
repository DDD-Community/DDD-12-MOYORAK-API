package com.moyorak.api.party.repository;

import com.moyorak.api.party.domain.Party;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PartyRepository extends JpaRepository<Party, Long> {

    Optional<Party> findByIdAndUseTrue(Long id);
}
