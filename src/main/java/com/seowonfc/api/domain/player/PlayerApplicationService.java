package com.seowonfc.api.domain.player;

import com.seowonfc.api.common.CustomException;
import com.seowonfc.api.common.ErrorCode;
import com.seowonfc.api.domain.player.dto.PlayerApplicationRequest;
import com.seowonfc.api.domain.player.dto.PlayerApplicationResponse;
import com.seowonfc.api.domain.user.User;
import com.seowonfc.api.domain.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PlayerApplicationService {

    private final PlayerApplicationRepository applicationRepository;
    private final PlayerRepository playerRepository;
    private final UserRepository userRepository;

    @Transactional
    public Long apply(Long userId, PlayerApplicationRequest request) {
        User applicant = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND));

        PlayerApplication application = PlayerApplication.builder()
                .applicant(applicant)
                .name(request.name())
                .backNumber(request.backNumber())
                .position(request.position())
                .nationality(request.nationality())
                .profileImageUrl(request.profileImageUrl())
                .build();

        return applicationRepository.save(application).getId();
    }

    public Page<PlayerApplicationResponse> getMyApplications(Long userId, Pageable pageable) {
        return applicationRepository.findByApplicantId(userId, pageable).map(PlayerApplicationResponse::from);
    }

    // ---- 관리자용 ----
    public Page<PlayerApplicationResponse> getPending(Pageable pageable) {
        return applicationRepository.findByStatus(ApplicationStatus.PENDING, pageable)
                .map(PlayerApplicationResponse::from);
    }

    @Transactional
    public Long approve(Long applicationId) {
        PlayerApplication application = findById(applicationId);
        application.approve();

        Player player = Player.builder()
                .name(application.getName())
                .backNumber(application.getBackNumber())
                .position(application.getPosition())
                .nationality(application.getNationality())
                .profileImageUrl(application.getProfileImageUrl())
                .build();

        return playerRepository.save(player).getId();
    }

    @Transactional
    public void reject(Long applicationId, String reason) {
        findById(applicationId).reject(reason);
    }

    private PlayerApplication findById(Long id) {
        return applicationRepository.findById(id)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND));
    }
}