package com.classiverse.backend.domain.story.service;

import com.classiverse.backend.domain.common.YnType;
import com.classiverse.backend.domain.story.dto.StoryContentResponseDto;
import com.classiverse.backend.domain.story.dto.StoryResponseDto;
import com.classiverse.backend.domain.story.entity.StoryContent;
import com.classiverse.backend.domain.story.repository.StoryContentRepository;
import com.classiverse.backend.domain.story.repository.StoryIntroRepository;
import com.classiverse.backend.domain.story.repository.StoryRepository;
import com.classiverse.backend.domain.story.dto.CharacterIntroDto;
import com.classiverse.backend.domain.story.dto.StoryIntroResponseDto;
import com.classiverse.backend.domain.story.entity.Story;
import com.classiverse.backend.domain.unlock.entity.UserStoryUnlock;
import com.classiverse.backend.domain.unlock.repository.UserStoryUnlockRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StoryService {

    private final StoryRepository storyRepository;
    private final StoryIntroRepository storyIntroRepository;
    private final StoryContentRepository storyContentRepository;
    private final UserStoryUnlockRepository userStoryUnlockRepository;

    @Transactional(readOnly = true)
    // userId 파라미터 추가 필요!
    public List<StoryResponseDto> getStoriesByBookId(Long bookId, Long userId) {

        // 1. 해당 책의 모든 스토리 가져오기
        List<Story> stories = storyRepository.findAllByBook_BookIdOrderByEpisodeNumAsc(bookId);

        // 2. 유저의 해금 기록 가져오기 (속도 최적화를 위해 미리 다 가져옴)
        List<UserStoryUnlock> unlocks = userStoryUnlockRepository.findAllByUser_UserIdAndStory_Book_BookId(userId, bookId);

        // 해금된 스토리 ID 목록 추출
        Set<Long> unlockedStoryIds = unlocks.stream()
                .filter(u -> u.getUnlocked() == YnType.Y)
                .map(u -> u.getStory().getStoryId())
                .collect(Collectors.toSet());

        // 3. DTO 변환 (잠금 여부 판단)
        return stories.stream()
                .map(story -> {
                    boolean isLocked;

                    // 로직: 1~3화는 무조건 오픈, 4~5화는 해금 기록 확인
                    if (story.getEpisodeNum() <= 3) {
                        isLocked = false; // 기본 해금
                    } else {
                        // 해금 기록에 ID가 없으면 잠김(true)
                        isLocked = !unlockedStoryIds.contains(story.getStoryId());
                    }

                    return new StoryResponseDto(story, isLocked);
                })
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public StoryIntroResponseDto getStoryIntros(Long storyId, Long userId) {
        // 1. 스토리 조회 (잠금 확인 로직 포함 - 기존 유지)
        Story story = storyRepository.findById(storyId)
                .orElseThrow(() -> new IllegalArgumentException("해당 스토리를 찾을 수 없습니다. id=" + storyId));

        if (story.getEpisodeNum() > 3) {
            if (!userStoryUnlockRepository.existsByUser_UserIdAndStory_StoryId(userId, storyId)) {
                throw new IllegalArgumentException("이 스토리는 잠겨있습니다. 이전 에피소드를 먼저 완료해주세요.");
            }
        }

        // 2. 캐릭터 소개 리스트 조회 및 DTO 변환 (여기가 핵심!)
        List<CharacterIntroDto> intros = storyIntroRepository.findAllByStory_StoryIdOrderByCharacter_CharIdAsc(storyId)
                .stream()
                .map(intro -> {
                    // ★ 각 캐릭터(intro)에 맞는 첫 번째 콘텐츠 ID를 찾습니다.
                    Long firstContentId = storyContentRepository
                            .findFirstByStory_StoryIdAndCharacter_CharIdOrderBySeqAsc(storyId, intro.getCharacter().getCharId())
                            .map(StoryContent::getContentId)
                            .orElse(null); // 콘텐츠가 없으면 null

                    // ID를 포함하여 DTO 생성
                    return new CharacterIntroDto(intro, firstContentId);
                })
                .collect(Collectors.toList());

        // 3. 반환
        return new StoryIntroResponseDto(story.getTitle(), intros);
    }

    @Transactional(readOnly = true)
    public StoryContentResponseDto getStoryContent(Long storyId, Long characterId, Long contentId) {
        // 1. 현재 콘텐츠 조회
        StoryContent content = storyContentRepository.findById(contentId)
                .orElseThrow(() -> new IllegalArgumentException("해당 내용을 찾을 수 없습니다. id=" + contentId));

        // 2. 데이터 무결성 검증
        if (!content.getStory().getStoryId().equals(storyId)) {
            throw new IllegalArgumentException("이 스토리에 속한 내용이 아닙니다.");
        }
        if (!content.getCharacter().getCharId().equals(characterId)) {
            throw new IllegalArgumentException("이 캐릭터의 시점이 아닙니다.");
        }

        // 3. DTO 반환
        return new StoryContentResponseDto(content);
    }
}