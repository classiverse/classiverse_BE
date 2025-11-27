package com.classiverse.backend.domain.reading.service;

import com.classiverse.backend.domain.character.entity.StoryCharacter;
import com.classiverse.backend.domain.character.repository.StoryCharacterRepository;
import com.classiverse.backend.domain.closeness.entity.Closeness;
import com.classiverse.backend.domain.closeness.repository.ClosenessRepository;
import com.classiverse.backend.domain.reading.dto.ReadingCompleteDto;
import com.classiverse.backend.domain.reading.entity.ReadingProgress;
import com.classiverse.backend.domain.reading.repository.ReadingProgressRepository;
import com.classiverse.backend.domain.story.entity.Story;
import com.classiverse.backend.domain.story.entity.StoryIntro;
import com.classiverse.backend.domain.story.repository.StoryIntroRepository;
import com.classiverse.backend.domain.story.repository.StoryRepository;
import com.classiverse.backend.domain.user.entity.User;
import com.classiverse.backend.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class ReadingService {

    private final ReadingProgressRepository readingProgressRepository;
    private final ClosenessRepository closenessRepository;
    private final StoryCharacterRepository characterRepository;
    private final StoryRepository storyRepository;
    private final UserRepository userRepository;
    private final StoryIntroRepository storyIntroRepository; // ★ 주입 추가

    @Transactional
    public ReadingCompleteDto completeReading(Long storyId, Long charId, Long userId) {
        // 1. 엔티티 조회 (기존 로직 유지)
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("유저 없음"));
        Story story = storyRepository.findById(storyId)
                .orElseThrow(() -> new IllegalArgumentException("스토리 없음"));
        StoryCharacter character = characterRepository.findById(charId)
                .orElseThrow(() -> new IllegalArgumentException("캐릭터 없음"));

        // 2. 독서 기록 저장 (기존 로직 유지)
        if (!readingProgressRepository.existsByUser_UserIdAndStory_StoryIdAndCharacter_CharId(userId, storyId, charId)) {
            ReadingProgress progress = new ReadingProgress(user, story, character, LocalDateTime.now());
            readingProgressRepository.save(progress);
        }

        // 3. 친밀도 재계산 및 저장 (기존 로직 유지)
        long readCount = readingProgressRepository.countByUser_UserIdAndCharacter_CharId(userId, charId);
        int newClosenessScore = (int) (readCount * 20);
        if (newClosenessScore > 100) newClosenessScore = 100;

        Closeness closeness = closenessRepository.findByUser_UserIdAndCharacter_CharId(userId, charId)
                .orElseGet(() -> new Closeness(user, character, 0));
        closeness.updateScore(newClosenessScore);
        closenessRepository.save(closeness);

        // 4. StoryIntro 조회하여 Final Text 가져오기
        StoryIntro storyIntro = storyIntroRepository.findByStory_StoryIdAndCharacter_CharId(storyId, charId)
                .orElseThrow(() -> new IllegalArgumentException("스토리 소개(Intro) 데이터가 없습니다."));

        // 5. 결과 반환 (수정된 DTO 생성자 사용)
        return new ReadingCompleteDto(
                story.getTitle(),
                character.getName(),
                newClosenessScore,
                storyIntro.getFinalText() // final_text 반환
        );
    }
}