package com.classiverse.backend.domain.character.controller;

import com.classiverse.backend.domain.character.dto.CharacterResponseDto;
import com.classiverse.backend.domain.character.dto.CharacterDetailResponseDto;
import com.classiverse.backend.domain.character.service.CharacterService;
import com.classiverse.backend.domain.user.security.CustomUserPrincipal; // [추가] 인증 객체 Import
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal; // [추가] 어노테이션 Import
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class CharacterController {

    private final CharacterService characterService;

    // 캐릭터 및 친밀도 조회 API
    // GET /api/books/{bookId}/characters
    @GetMapping("/api/books/{bookId}/characters")
    public ResponseEntity<List<CharacterResponseDto>> getCharacters(
            @PathVariable Long bookId,
            @AuthenticationPrincipal CustomUserPrincipal principal // [수정] 로그인한 유저 정보 주입
    ) {
        Long userId = principal.getUserId();

        List<CharacterResponseDto> response = characterService.getCharactersByBook(bookId, userId); // tempUserId -> userId로 변경
        return ResponseEntity.ok(response);
    }

    // 캐릭터 상세 정보 조회 API
    // GET /api/books/{bookId}/characters/{characterId}
    @GetMapping("/api/books/{bookId}/characters/{characterId}")
    public ResponseEntity<CharacterDetailResponseDto> getCharacterDetail(
            @PathVariable Long bookId,
            @PathVariable Long characterId,
            @AuthenticationPrincipal CustomUserPrincipal principal // [수정] 로그인한 유저 정보 주입
    ) {
        Long userId = principal.getUserId();

        CharacterDetailResponseDto response = characterService.getCharacterDetail(bookId, characterId, userId); // tempUserId -> userId로 변경
        return ResponseEntity.ok(response);
    }
}