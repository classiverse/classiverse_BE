package com.classiverse.backend.domain.story.repository;

import com.classiverse.backend.domain.story.entity.StoryIntro;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StoryIntroRepository extends JpaRepository<StoryIntro, Long> {

    List<StoryIntro> findAllByStory_StoryIdOrderByCharacter_CharIdAsc(Long storyId);

    Optional<StoryIntro> findByStory_StoryIdAndCharacter_CharId(Long storyId, Long charId);
}