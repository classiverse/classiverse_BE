package com.classiverse.backend.domain.story.controller;

import com.classiverse.backend.domain.story.dto.StoryContentResponseDto;
import com.classiverse.backend.domain.story.dto.StoryIntroResponseDto;
import com.classiverse.backend.domain.story.dto.StoryResponseDto;
import com.classiverse.backend.domain.story.service.StoryService;
import com.classiverse.backend.domain.user.security.CustomUserPrincipal; // [추가] 인증 객체 타입 변경
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class StoryController {

    private final StoryService storyService;

    // 스토리 목록 조회 API
    // GET /api/books/{bookId}/stories
    @GetMapping("/api/books/{bookId}/stories")
    public ResponseEntity<List<StoryResponseDto>> getStoriesByBook(
            @PathVariable Long bookId,
            @AuthenticationPrincipal CustomUserPrincipal principal // [수정] User -> CustomUserPrincipal 변경 (필터와 타입 일치)
    ) {

        // SecurityConfig에서 인증된 요청만 들어오므로 principal은 null이 아님을 보장받음
        Long userId = principal.getUserId();

        // Service 호출 시 userId 전달
        List<StoryResponseDto> response = storyService.getStoriesByBookId(bookId, userId);
        return ResponseEntity.ok(response);
    }

    // 스토리별 캐릭터 소개 API (인트로)
    // GET /api/stories/{storyId}/intro
    @GetMapping("/api/stories/{storyId}/intro")
    public ResponseEntity<StoryIntroResponseDto> getStoryIntros(
            @PathVariable Long storyId,
            @AuthenticationPrincipal CustomUserPrincipal principal // [수정] User -> CustomUserPrincipal 변경
    ) {
        // 실제 유저 ID 추출
        Long userId = principal.getUserId();

        // Service에 userId 전달
        StoryIntroResponseDto response = storyService.getStoryIntros(storyId, userId);
        return ResponseEntity.ok(response);
    }

    // 스토리 뷰어 - 단건 내용 조회 API
    // GET /api/stories/{storyId}/characters/{characterId}/contents/{contentId}
    @GetMapping("/api/stories/{storyId}/characters/{characterId}/contents/{contentId}")
    public ResponseEntity<StoryContentResponseDto> getStoryContent(
            @PathVariable Long storyId,
            @PathVariable Long characterId,
            @PathVariable Long contentId) {

        StoryContentResponseDto response = storyService.getStoryContent(storyId, characterId, contentId);
        return ResponseEntity.ok(response);
    }
}