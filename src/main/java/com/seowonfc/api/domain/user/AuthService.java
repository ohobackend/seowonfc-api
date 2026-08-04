package com.seowonfc.api.domain.user;

import com.seowonfc.api.common.CustomException;
import com.seowonfc.api.common.ErrorCode;
import com.seowonfc.api.config.JwtProvider;
import com.seowonfc.api.domain.user.dto.*;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtProvider jwtProvider;

    public void signup(SignupRequest request) {
        if (userRepository.existsByEmail(request.email())) {
            throw new CustomException(ErrorCode.CONFLICT);
        }
        User user = User.builder()
                .email(request.email())
                .password(passwordEncoder.encode(request.password()))
                .name(request.name())
                .role(Role.USER)
                .build();
        userRepository.save(user);
    }

    public LoginResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new CustomException(ErrorCode.UNAUTHORIZED));

        if (!passwordEncoder.matches(request.password(), user.getPassword())) {
            throw new CustomException(ErrorCode.UNAUTHORIZED);
        }

        String access = jwtProvider.createAccessToken(user.getId(), user.getRole().name());
        String refresh = jwtProvider.createRefreshToken(user.getId(), user.getRole().name());
        return new LoginResponse(access, refresh, UserResponse.from(user));
    }
}