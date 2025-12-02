package com.classiverse.backend.domain.story.repository;

import com.classiverse.backend.domain.story.entity.StoryContent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface StoryContentRepository extends JpaRepository<StoryContent, Long> {

    // (기존) 다음 페이지 찾기
    Optional<StoryContent> findFirstByStory_StoryIdAndCharacter_CharIdAndSeqGreaterThanOrderBySeqAsc(
            Long storyId, Long charId, Integer seq
    );

    // ★ [수정] 스토리 + 캐릭터별 첫 번째 콘텐츠 찾기
    Optional<StoryContent> findFirstByStory_StoryIdAndCharacter_CharIdOrderBySeqAsc(Long storyId, Long charId);
}