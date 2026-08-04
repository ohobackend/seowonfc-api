package com.seowonfc.api.domain.player;

import com.seowonfc.api.common.CustomException;
import com.seowonfc.api.common.ErrorCode;
import com.seowonfc.api.domain.player.dto.PlayerRequest;
import com.seowonfc.api.domain.player.dto.PlayerResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PlayerService {

    private final PlayerRepository playerRepository;

    public Page<PlayerResponse> getList(Position position, Pageable pageable) {
        Page<Player> page = (position == null)
                ? playerRepository.findAll(pageable)
                : playerRepository.findByPosition(position, pageable);
        return page.map(PlayerResponse::from);
    }

    public PlayerResponse getDetail(Long id) {
        return PlayerResponse.from(findById(id));
    }

    @Transactional
    public Long create(PlayerRequest request) {
        Player player = Player.builder()
                .name(request.name())
                .backNumber(request.backNumber())
                .position(request.position())
                .nationality(request.nationality())
                .profileImageUrl(request.profileImageUrl())
                .build();
        return playerRepository.save(player).getId();
    }

    @Transactional
    public void update(Long id, PlayerRequest request) {
        Player player = findById(id);
        player.update(request.name(), request.backNumber(), request.position(),
                request.nationality(), request.profileImageUrl());
    }

    @Transactional
    public void delete(Long id) {
        playerRepository.delete(findById(id));
    }

    private Player findById(Long id) {
        return playerRepository.findById(id)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND));
    }
}