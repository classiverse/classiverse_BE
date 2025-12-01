package com.classiverse.backend.domain.character.service;

import com.classiverse.backend.domain.character.dto.CharacterResponseDto;
import com.classiverse.backend.domain.character.entity.StoryCharacter;
import com.classiverse.backend.domain.character.repository.StoryCharacterRepository;
import com.classiverse.backend.domain.closeness.entity.Closeness;
import com.classiverse.backend.domain.closeness.repository.ClosenessRepository;
import com.classiverse.backend.domain.character.dto.CharacterDetailResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CharacterService {

    private final StoryCharacterRepository storyCharacterRepository;
    private final ClosenessRepository closenessRepository;

    @Transactional(readOnly = true)
    public List<CharacterResponseDto> getCharactersByBook(Long bookId, Long userId) {
        // 1. 이 책의 모든 캐릭터 가져오기
        List<StoryCharacter> characters = storyCharacterRepository.findAllByBook_BookId(bookId);

        // 2. 내(userId)가 이 책에서 쌓은 친밀도 데이터 가져오기
        List<Closeness> myClosenessList = closenessRepository.findByUser_UserIdAndCharacter_Book_BookId(userId, bookId);

        // 3. 친밀도 데이터를 "캐릭터 ID : 친밀도 점수" 형태의 Map으로 변환 (검색 속도 UP)
        Map<Long, Integer> closenessMap = myClosenessList.stream()
                .collect(Collectors.toMap(
                        c -> c.getCharacter().getCharId(), // Key: 캐릭터 ID
                        Closeness::getCloseness            // Value: 점수
                ));

        // 4. 캐릭터 목록을 돌면서 친밀도를 매칭 (없으면 0)
        return characters.stream()
                .map(character -> {
                    Integer score = closenessMap.getOrDefault(character.getCharId(), 0);
                    return new CharacterResponseDto(character, score);
                })
                .collect(Collectors.toList());
    }

    // 캐릭터 상세 정보 조회
    @Transactional(readOnly = true)
    public CharacterDetailResponseDto getCharacterDetail(Long bookId, Long characterId, Long userId) {
        // 1. 캐릭터 조회 (없으면 에러)
        StoryCharacter character = storyCharacterRepository.findById(characterId)
                .orElseThrow(() -> new IllegalArgumentException("해당 캐릭터를 찾을 수 없습니다. id=" + characterId));

        // (선택) URL의 bookId와 캐릭터의 실제 bookId가 다르면 에러 처리 (데이터 무결성 체크)
        if (!character.getBook().getBookId().equals(bookId)) {
            throw new IllegalArgumentException("이 책에 속한 캐릭터가 아닙니다.");
        }

        // 2. 친밀도 조회 (없으면 0점 처리)
        Integer closenessScore = closenessRepository.findByUser_UserIdAndCharacter_CharId(userId, characterId)
                .map(Closeness::getCloseness)
                .orElse(0);

        // 3. DTO 반환
        return new CharacterDetailResponseDto(character, closenessScore);
    }
}