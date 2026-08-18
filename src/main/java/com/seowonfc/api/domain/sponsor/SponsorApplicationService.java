package com.seowonfc.api.domain.sponsor;

import com.seowonfc.api.common.CustomException;
import com.seowonfc.api.common.ErrorCode;
import com.seowonfc.api.domain.image.ImageUploadService;
import com.seowonfc.api.domain.sponsor.dto.SponsorApplicationRequest;
import com.seowonfc.api.domain.sponsor.dto.SponsorApplicationResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SponsorApplicationService {

    private final SponsorApplicationRepository applicationRepository;
    private final SponsorRepository sponsorRepository;
    private final ImageUploadService imageUploadService;

    @Transactional
    public Long apply(SponsorApplicationRequest request, MultipartFile file) {
        String logoUrl = null;
        if (file != null && !file.isEmpty()) {
            logoUrl = imageUploadService.upload(file, "sponsor-applications").url();
        }

        SponsorApplication application = SponsorApplication.builder()
                .companyName(request.companyName())
                .contactName(request.contactName())
                .contactEmail(request.contactEmail())
                .contactPhone(request.contactPhone())
                .desiredTier(request.desiredTier())
                .logoUrl(logoUrl)
                .message(request.message())
                .linkUrl(request.linkUrl())
                .build();

        return applicationRepository.save(application).getId();
    }

    public Page<SponsorApplicationResponse> getPending(Pageable pageable) {
        return applicationRepository.findByStatus(SponsorApplicationStatus.PENDING, pageable)
                .map(SponsorApplicationResponse::from);
    }

    @Transactional
    public Long approve(Long applicationId) {
        SponsorApplication application = findById(applicationId);
        application.approve();

        Sponsor sponsor = Sponsor.builder()
                .name(application.getCompanyName())
                .logoUrl(application.getLogoUrl())
                .tier(application.getDesiredTier())
                .linkUrl(application.getLinkUrl())
                .build();

        return sponsorRepository.save(sponsor).getId();
    }

    @Transactional
    public void reject(Long applicationId, String reason) {
        findById(applicationId).reject(reason);
    }

    private SponsorApplication findById(Long id) {
        return applicationRepository.findById(id)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND));
    }
}
