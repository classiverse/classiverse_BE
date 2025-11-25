package com.classiverse.backend.domain.story.service;

import com.classiverse.backend.domain.story.dto.StoryResponseDto;
import com.classiverse.backend.domain.story.repository.StoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StoryService {

    private final StoryRepository storyRepository;

    @Transactional(readOnly = true)
    public List<StoryResponseDto> getStoriesByBookId(Long bookId) {
        // DB에서 bookId로 스토리를 조회 (순서대로 정렬되어 옴)
        return storyRepository.findAllByBook_BookIdOrderByEpisodeNumAsc(bookId)
                .stream()
                .map(StoryResponseDto::new)
                .collect(Collectors.toList());
    }
}