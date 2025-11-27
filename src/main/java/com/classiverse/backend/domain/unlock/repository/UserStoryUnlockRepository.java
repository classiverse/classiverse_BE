package com.classiverse.backend.domain.unlock.repository;

import com.classiverse.backend.domain.unlock.entity.UserStoryUnlock;
import com.classiverse.backend.domain.unlock.entity.UserStoryUnlockId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserStoryUnlockRepository extends JpaRepository<UserStoryUnlock, UserStoryUnlockId> {

    // 리스트 조회용
    List<UserStoryUnlock> findAllByUser_UserIdAndStory_Book_BookId(Long userId, Long bookId);

    // 단건 조회용: 특정 유저가 특정 스토리를 해금했는지 확인 (true/false)
    boolean existsByUser_UserIdAndStory_StoryId(Long userId, Long storyId);
}