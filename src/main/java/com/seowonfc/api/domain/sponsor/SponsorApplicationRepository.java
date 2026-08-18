package com.seowonfc.api.domain.sponsor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SponsorApplicationRepository extends JpaRepository<SponsorApplication, Long> {
    Page<SponsorApplication> findByStatus(SponsorApplicationStatus status, Pageable pageable);
}
