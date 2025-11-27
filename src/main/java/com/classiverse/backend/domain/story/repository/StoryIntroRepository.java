package com.classiverse.backend.domain.story.repository;

import com.classiverse.backend.domain.story.entity.StoryIntro;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StoryIntroRepository extends JpaRepository<StoryIntro, Long> {

    // StoryId로 조회하되, 캐릭터 ID 순서대로 정렬 (Order By Character_CharId Asc)
    List<StoryIntro> findAllByStory_StoryIdOrderByCharacter_CharIdAsc(Long storyId);
}