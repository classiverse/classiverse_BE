package com.classiverse.backend.domain.user.controller;

import com.classiverse.backend.domain.user.dto.ProfileMeResponseDto;
import com.classiverse.backend.domain.user.service.ProfileService;
import com.classiverse.backend.domain.user.security.CustomUserPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/profile")
@RequiredArgsConstructor
public class ProfileController {

    private final ProfileService profileService;

    @GetMapping("/me")
    public ProfileMeResponseDto getMyProfile(@AuthenticationPrincipal CustomUserPrincipal principal) {
        Long userId = principal.getUserId(); // ★ 여기에서 userId만 꺼내면 됩니다.
        return profileService.getMyProfile(userId);
    }

    @PutMapping("/nickname")
    public void updateNickname(@AuthenticationPrincipal CustomUserPrincipal principal,
                               @RequestBody NicknameUpdateRequest request) {
        Long userId = principal.getUserId();
        profileService.updateNickname(userId, request.nickname());
    }

    public record NicknameUpdateRequest(String nickname) {}
}