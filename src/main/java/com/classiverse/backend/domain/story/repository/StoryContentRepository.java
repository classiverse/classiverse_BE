package com.classiverse.backend.domain.story.repository;

import com.classiverse.backend.domain.story.entity.StoryContent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface StoryContentRepository extends JpaRepository<StoryContent, Long> {
    // 다음 페이지 찾기: 현재 seq보다 큰 것 중 가장 작은(첫 번째) 것
    Optional<StoryContent> findFirstByStory_StoryIdAndCharacter_CharIdAndSeqGreaterThanOrderBySeqAsc(
            Long storyId, Long charId, Integer seq
    );
}