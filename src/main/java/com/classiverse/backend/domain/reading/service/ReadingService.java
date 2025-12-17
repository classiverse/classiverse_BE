package com.classiverse.backend.domain.reading.service;

import com.classiverse.backend.domain.character.entity.StoryCharacter;
import com.classiverse.backend.domain.character.repository.StoryCharacterRepository;
import com.classiverse.backend.domain.closeness.entity.Closeness;
import com.classiverse.backend.domain.closeness.repository.ClosenessRepository;
import com.classiverse.backend.domain.common.YnType;
import com.classiverse.backend.domain.reading.dto.ReadingCompleteDto;
import com.classiverse.backend.domain.reading.entity.ReadingProgress;
import com.classiverse.backend.domain.reading.repository.ReadingProgressRepository;
import com.classiverse.backend.domain.story.entity.Story;
import com.classiverse.backend.domain.story.entity.StoryIntro;
import com.classiverse.backend.domain.story.repository.StoryIntroRepository;
import com.classiverse.backend.domain.story.repository.StoryRepository;
import com.classiverse.backend.domain.unlock.entity.UserStoryUnlock;
import com.classiverse.backend.domain.unlock.entity.UserStoryUnlockId;
import com.classiverse.backend.domain.unlock.repository.UserStoryUnlockRepository;
import com.classiverse.backend.domain.user.entity.User;
import com.classiverse.backend.domain.user.repository.UserRepository;
// [추가] 카테고리 확장을 위한 임포트
import com.classiverse.backend.domain.category.repository.CategoryRepository;
import com.classiverse.backend.domain.unlock.repository.UserCategoryUnlockRepository;
import com.classiverse.backend.domain.unlock.entity.UserCategoryUnlock;
import com.classiverse.backend.domain.unlock.entity.UserCategoryUnlockId;
import com.classiverse.backend.domain.category.entity.Category;

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
    private final StoryIntroRepository storyIntroRepository;
    private final UserStoryUnlockRepository userStoryUnlockRepository;

    // [추가] 다음 성운(카테고리) 해금을 위한 저장소
    private final CategoryRepository categoryRepository;
    private final UserCategoryUnlockRepository userCategoryUnlockRepository;

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

        // 성운 확장 로직: 친밀도 100 달성 시 다음 카테고리 오픈
        if (newClosenessScore >= 100) {
            unlockNextCategory(user, story.getBook().getCategory());
        }

        // 4. StoryIntro 조회하여 Final Text 가져오기
        StoryIntro storyIntro = storyIntroRepository.findByStory_StoryIdAndCharacter_CharId(storyId, charId)
                .orElseThrow(() -> new IllegalArgumentException("스토리 소개(Intro) 데이터가 없습니다."));

        // 5. 스토리 해금 로직 실행 (기존 로직 유지)
        checkAndUnlockNextStory(story, user);

        // 6. 결과 반환
        return new ReadingCompleteDto(
                story.getTitle(),
                character.getName(),
                newClosenessScore,
                storyIntro.getFinalText(),
                character.getCharImage(),
                story.getBook().getBookId()
        );
    }

    // [추가] 다음 카테고리 자동 해금 헬퍼
    private void unlockNextCategory(User user, Category currentCategory) {
        // 1. 다음 카테고리 ID 계산 (현재 ID + 1)
        Long nextCategoryId = currentCategory.getCategoryId() + 1;

        // 2. 다음 카테고리가 DB에 존재하는지 확인
        categoryRepository.findById(nextCategoryId).ifPresent(nextCategory -> {

            // 3. 이미 열려있는지 확인 (중복 저장 방지)
            UserCategoryUnlockId unlockId = new UserCategoryUnlockId(user.getUserId(), nextCategoryId);

            if (!userCategoryUnlockRepository.existsById(unlockId)) {
                // 4. 잠금 해제 데이터 저장
                UserCategoryUnlock newUnlock = new UserCategoryUnlock(
                        user,
                        nextCategory,
                        YnType.Y
                );
                userCategoryUnlockRepository.save(newUnlock);
            }
        });
    }

    // 기존 해금 로직 (유지)
    private void checkAndUnlockNextStory(Story currentStory, User user) {
        Long bookId = currentStory.getBook().getBookId();
        int episodeNum = currentStory.getEpisodeNum();

        if (episodeNum >= 1 && episodeNum <= 3) {
            boolean read1 = isEpisodeRead(bookId, 1, user.getUserId());
            boolean read2 = isEpisodeRead(bookId, 2, user.getUserId());
            boolean read3 = isEpisodeRead(bookId, 3, user.getUserId());

            if (read1 && read2 && read3) {
                unlockStoryByEpisodeNum(bookId, 4, user);
            }
        }

        if (episodeNum == 4) {
            unlockStoryByEpisodeNum(bookId, 5, user);
        }
    }

    private boolean isEpisodeRead(Long bookId, int episodeNum, Long userId) {
        return storyRepository.findByBook_BookIdAndEpisodeNum(bookId, episodeNum)
                .map(s -> readingProgressRepository.existsByUser_UserIdAndStory_StoryId(userId, s.getStoryId()))
                .orElse(false);
    }

    private void unlockStoryByEpisodeNum(Long bookId, int episodeNum, User user) {
        storyRepository.findByBook_BookIdAndEpisodeNum(bookId, episodeNum).ifPresent(targetStory -> {
            UserStoryUnlockId id = new UserStoryUnlockId(user.getUserId(), targetStory.getStoryId());
            if (!userStoryUnlockRepository.existsById(id)) {
                userStoryUnlockRepository.save(new UserStoryUnlock(user, targetStory, YnType.Y));
            }
        });
    }
}