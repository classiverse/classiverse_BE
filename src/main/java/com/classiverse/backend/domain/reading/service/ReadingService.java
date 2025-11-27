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

        // 5. 스토리 해금 로직 실행
        checkAndUnlockNextStory(story, user);

        // 6. 결과 반환 (수정된 DTO 생성자 사용)
        return new ReadingCompleteDto(
                story.getTitle(),
                character.getName(),
                newClosenessScore,
                storyIntro.getFinalText() // final_text 반환
        );
    }
    // 해금 조건 체크 및 실행
    private void checkAndUnlockNextStory(Story currentStory, User user) {
        Long bookId = currentStory.getBook().getBookId();
        int episodeNum = currentStory.getEpisodeNum();

        // 조건 1: 1, 2, 3화를 모두 읽으면 -> 4화 해금
        if (episodeNum >= 1 && episodeNum <= 3) {
            // 1, 2, 3화의 ID를 가져와서 다 읽었는지 체크해야 함
            // (단순화를 위해 에피소드 번호로 체크)
            boolean read1 = isEpisodeRead(bookId, 1, user.getUserId());
            boolean read2 = isEpisodeRead(bookId, 2, user.getUserId());
            boolean read3 = isEpisodeRead(bookId, 3, user.getUserId());

            if (read1 && read2 && read3) {
                unlockStoryByEpisodeNum(bookId, 4, user);
            }
        }

        // 조건 2: 4화를 읽으면 -> 5화 해금
        if (episodeNum == 4) {
            unlockStoryByEpisodeNum(bookId, 5, user);
        }
    }

    // 에피소드 번호로 읽음 여부 확인 헬퍼
    private boolean isEpisodeRead(Long bookId, int episodeNum, Long userId) {
        return storyRepository.findByBook_BookIdAndEpisodeNum(bookId, episodeNum)
                .map(s -> readingProgressRepository.existsByUser_UserIdAndStory_StoryId(userId, s.getStoryId()))
                .orElse(false);
    }

    // 에피소드 번호로 해금 처리 헬퍼
    private void unlockStoryByEpisodeNum(Long bookId, int episodeNum, User user) {
        storyRepository.findByBook_BookIdAndEpisodeNum(bookId, episodeNum).ifPresent(targetStory -> {
            // 이미 해금되었는지 확인 (중복 방지)
            UserStoryUnlockId id = new UserStoryUnlockId(user.getUserId(), targetStory.getStoryId());
            if (!userStoryUnlockRepository.existsById(id)) {
                // 해금 기록 저장 (unlocked = Y)
                // *UserStoryUnlock 엔티티에 생성자 필요 (없으면 추가해주세요)
                // UserStoryUnlock unlock = new UserStoryUnlock(user, targetStory, YnType.Y);
                // userStoryUnlockRepository.save(unlock);

                // (참고: UserStoryUnlock 엔티티에 아래와 같은 생성자가 있다고 가정합니다)
                userStoryUnlockRepository.save(new UserStoryUnlock(user, targetStory, YnType.Y));
            }
        });
    }
}