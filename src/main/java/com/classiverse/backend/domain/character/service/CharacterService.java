package com.classiverse.backend.domain.character.service;

import com.classiverse.backend.domain.character.dto.CharacterResponseDto;
import com.classiverse.backend.domain.character.entity.StoryCharacter;
import com.classiverse.backend.domain.character.repository.StoryCharacterRepository;
import com.classiverse.backend.domain.closeness.entity.Closeness;
import com.classiverse.backend.domain.closeness.repository.ClosenessRepository;
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
}