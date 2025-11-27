package com.classiverse.backend.domain.story.service;

import com.classiverse.backend.domain.story.dto.StoryContentResponseDto;
import com.classiverse.backend.domain.story.dto.StoryResponseDto;
import com.classiverse.backend.domain.story.entity.StoryContent;
import com.classiverse.backend.domain.story.repository.StoryContentRepository;
import com.classiverse.backend.domain.story.repository.StoryIntroRepository;
import com.classiverse.backend.domain.story.repository.StoryRepository;
import com.classiverse.backend.domain.story.dto.CharacterIntroDto;
import com.classiverse.backend.domain.story.dto.StoryIntroResponseDto;
import com.classiverse.backend.domain.story.entity.Story;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StoryService {

    private final StoryRepository storyRepository;
    private final StoryIntroRepository storyIntroRepository;
    private final StoryContentRepository storyContentRepository;

    @Transactional(readOnly = true)
    public List<StoryResponseDto> getStoriesByBookId(Long bookId) {
        // DB에서 bookId로 스토리를 조회 (순서대로 정렬되어 옴)
        return storyRepository.findAllByBook_BookIdOrderByEpisodeNumAsc(bookId)
                .stream()
                .map(StoryResponseDto::new)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public StoryIntroResponseDto getStoryIntros(Long storyId) {
        // 1. 스토리 제목 조회 (없으면 에러)
        Story story = storyRepository.findById(storyId)
                .orElseThrow(() -> new IllegalArgumentException("해당 스토리를 찾을 수 없습니다. id=" + storyId));

        // 2. 해당 스토리의 캐릭터 소개 리스트 조회
        List<CharacterIntroDto> intros = storyIntroRepository.findAllByStory_StoryIdOrderByCharacter_CharIdAsc(storyId)
                .stream()
                .map(CharacterIntroDto::new)
                .collect(Collectors.toList());

        // 3. 제목과 리스트를 묶어서 반환
        return new StoryIntroResponseDto(story.getTitle(), intros);
    }
    @Transactional(readOnly = true)
    public StoryContentResponseDto getStoryContent(Long storyId, Long characterId, Long contentId) {
        // 1. 현재 콘텐츠 조회
        StoryContent currentContent = storyContentRepository.findById(contentId)
                .orElseThrow(() -> new IllegalArgumentException("해당 내용을 찾을 수 없습니다. id=" + contentId));

        // 데이터 무결성 검증 (생략 가능하지만 안전을 위해 유지)
        if (!currentContent.getStory().getStoryId().equals(storyId)) {
            throw new IllegalArgumentException("이 스토리에 속한 내용이 아닙니다.");
        }
        if (!currentContent.getCharacter().getCharId().equals(characterId)) {
            throw new IllegalArgumentException("이 캐릭터의 시점이 아닙니다.");
        }

        // 2. 다음 페이지 ID 조회 (Next)
        StoryContent nextContent = storyContentRepository.findFirstByStory_StoryIdAndCharacter_CharIdAndSeqGreaterThanOrderBySeqAsc(
                storyId, characterId, currentContent.getSeq()
        ).orElse(null);

        // 3. ID 추출 (없으면 null -> 마지막 페이지)
        Long nextId = (nextContent != null) ? nextContent.getContentId() : null;

        // 4. DTO 반환
        return new StoryContentResponseDto(currentContent, nextId);
    }
}