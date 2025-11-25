package com.classiverse.backend.domain.character.controller;

import com.classiverse.backend.domain.character.dto.CharacterResponseDto;
import com.classiverse.backend.domain.character.dto.CharacterDetailResponseDto;
import com.classiverse.backend.domain.character.service.CharacterService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<List<CharacterResponseDto>> getCharacters(@PathVariable Long bookId) {

        // ★ TODO: 나중에 Spring Security 적용 시, 여기서 로그인한 유저 ID를 가져오도록 수정해야 함!
        Long tempUserId = 1L; // 임시 유저 ID

        List<CharacterResponseDto> response = characterService.getCharactersByBook(bookId, tempUserId);
        return ResponseEntity.ok(response);
    }

    // 캐릭터 상세 정보 조회 API
    // GET /api/books/{bookId}/characters/{characterId}
    @GetMapping("/api/books/{bookId}/characters/{characterId}")
    public ResponseEntity<CharacterDetailResponseDto> getCharacterDetail(
            @PathVariable Long bookId,
            @PathVariable Long characterId) {

        Long tempUserId = 1L; // 임시 유저 ID

        CharacterDetailResponseDto response = characterService.getCharacterDetail(bookId, characterId, tempUserId);
        return ResponseEntity.ok(response);
    }
}