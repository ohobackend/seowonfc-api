package com.seowonfc.api.domain.sponsor;

import com.seowonfc.api.common.CustomException;
import com.seowonfc.api.common.ErrorCode;
import com.seowonfc.api.domain.sponsor.dto.SponsorRequest;
import com.seowonfc.api.domain.sponsor.dto.SponsorResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SponsorService {

    private final SponsorRepository sponsorRepository;

    public List<SponsorResponse> getList(SponsorTier tier) {
        List<Sponsor> sponsors = (tier == null)
                ? sponsorRepository.findAll()
                : sponsorRepository.findByTier(tier);
        return sponsors.stream().map(SponsorResponse::from).toList();
    }

    @Transactional
    public Long create(SponsorRequest request) {
        Sponsor sponsor = Sponsor.builder()
                .name(request.name())
                .logoUrl(request.logoUrl())
                .tier(request.tier())
                .linkUrl(request.linkUrl())
                .build();
        return sponsorRepository.save(sponsor).getId();
    }

    @Transactional
    public void update(Long id, SponsorRequest request) {
        Sponsor sponsor = findById(id);
        sponsor.update(request.name(), request.logoUrl(), request.tier(), request.linkUrl());
    }

    @Transactional
    public void delete(Long id) {
        sponsorRepository.delete(findById(id));
    }

    private Sponsor findById(Long id) {
        return sponsorRepository.findById(id)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND));
    }
}