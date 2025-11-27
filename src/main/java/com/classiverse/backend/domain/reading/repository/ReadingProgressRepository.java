package com.classiverse.backend.domain.reading.repository;

import com.classiverse.backend.domain.reading.entity.ReadingProgress;
import com.classiverse.backend.domain.reading.entity.ReadingProgressId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReadingProgressRepository extends JpaRepository<ReadingProgress, ReadingProgressId> {

    // 내(User)가 이 캐릭터(Character)의 스토리를 몇 개나 읽었는지 카운트
    long countByUser_UserIdAndCharacter_CharId(Long userId, Long charId);

    // 이미 읽은 기록이 있는지 확인 (중복 저장 방지용)
    boolean existsByUser_UserIdAndStory_StoryIdAndCharacter_CharId(Long userId, Long storyId, Long charId);
}