package com.classiverse.backend.domain.character.repository;

import com.classiverse.backend.domain.character.entity.StoryCharacter;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StoryCharacterRepository extends JpaRepository<StoryCharacter, Long> {
    List<StoryCharacter> findAllByBook_BookId(Long bookId);
}