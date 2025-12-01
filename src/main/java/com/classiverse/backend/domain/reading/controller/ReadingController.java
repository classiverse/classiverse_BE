package com.classiverse.backend.domain.reading.controller;

import com.classiverse.backend.domain.reading.dto.ReadingCompleteDto;
import com.classiverse.backend.domain.reading.service.ReadingService;
import com.classiverse.backend.domain.user.security.CustomUserPrincipal; // [추가] 인증 객체 타입 변경
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class ReadingController {

    private final ReadingService readingService;

    // 독서 완료 및 친밀도 업데이트 API
    // POST /api/stories/{storyId}/characters/{characterId}/complete
    @PostMapping("/api/stories/{storyId}/characters/{characterId}/complete")
    public ResponseEntity<ReadingCompleteDto> completeStory(
            @PathVariable Long storyId,
            @PathVariable Long characterId,
            @AuthenticationPrincipal CustomUserPrincipal principal // [수정] User -> CustomUserPrincipal 변경
    ) {
        // 실제 유저 ID 추출
        Long userId = principal.getUserId();

        ReadingCompleteDto response = readingService.completeReading(storyId, characterId, userId);
        return ResponseEntity.ok(response);
    }
}