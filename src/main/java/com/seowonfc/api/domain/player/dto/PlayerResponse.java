package com.seowonfc.api.domain.player.dto;

import com.seowonfc.api.domain.player.Player;
import com.seowonfc.api.domain.player.Position;

public record PlayerResponse(
        Long id, String name, Integer backNumber, Position position,
        String nationality, String profileImageUrl
) {
    public static PlayerResponse from(Player player) {
        return new PlayerResponse(player.getId(), player.getName(), player.getBackNumber(),
                player.getPosition(), player.getNationality(), player.getProfileImageUrl());
    }
}