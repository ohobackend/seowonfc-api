package com.seowonfc.api.domain.sponsor;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface SponsorRepository extends JpaRepository<Sponsor, Long> {
    List<Sponsor> findByTier(SponsorTier tier);
}