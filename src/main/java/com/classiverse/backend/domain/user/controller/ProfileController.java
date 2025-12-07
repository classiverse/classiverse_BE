package com.classiverse.backend.domain.user.controller;

import com.classiverse.backend.domain.character.dto.CharacterResponseDto;
import com.classiverse.backend.domain.user.dto.ProfileMeResponseDto;
import com.classiverse.backend.domain.user.security.CustomUserPrincipal;
import com.classiverse.backend.domain.user.service.ProfileService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/profile")
@RequiredArgsConstructor
public class ProfileController {

    private final ProfileService profileService;

    @GetMapping("/me")
    public ProfileMeResponseDto getMyProfile(@AuthenticationPrincipal CustomUserPrincipal principal) {
        Long userId = principal.getUserId();
        return profileService.getMyProfile(userId);
    }

    @PutMapping("/nickname")
    public void updateNickname(@AuthenticationPrincipal CustomUserPrincipal principal,
                               @RequestBody NicknameUpdateRequest request) {
        Long userId = principal.getUserId();
        profileService.updateNickname(userId, request.nickname());
    }

    // 닉네임 중복 확인 API
    @GetMapping("/nickname/check")
    public ResponseEntity<Boolean> checkNickname(@RequestParam String nickname) {
        return ResponseEntity.ok(profileService.checkNicknameDuplicate(nickname));
    }

    @GetMapping("/characters")
    public List<CharacterResponseDto> getMyCharacters(@AuthenticationPrincipal CustomUserPrincipal principal) {
        Long userId = principal.getUserId();
        return profileService.getMyCharacters(userId);
    }

    public record NicknameUpdateRequest(String nickname) {}
}