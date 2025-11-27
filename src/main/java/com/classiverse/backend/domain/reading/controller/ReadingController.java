package com.classiverse.backend.domain.reading.controller;

import com.classiverse.backend.domain.reading.dto.ReadingCompleteDto;
import com.classiverse.backend.domain.reading.service.ReadingService;
import com.classiverse.backend.domain.user.entity.User;
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
            @AuthenticationPrincipal User user
    ) {
        // 임시 유저 ID 처리 (로그인 미구현 시)
        Long userId = (user != null) ? user.getUserId() : 1L;

        ReadingCompleteDto response = readingService.completeReading(storyId, characterId, userId);
        return ResponseEntity.ok(response);
    }
}