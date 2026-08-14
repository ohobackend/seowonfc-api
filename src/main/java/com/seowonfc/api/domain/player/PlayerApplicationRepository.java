package com.seowonfc.api.domain.player;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PlayerApplicationRepository extends JpaRepository<PlayerApplication, Long> {
    Page<PlayerApplication> findByStatus(ApplicationStatus status, Pageable pageable);
    Page<PlayerApplication> findByApplicantId(Long applicantId, Pageable pageable);
}