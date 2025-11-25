package com.classiverse.backend.domain.story.repository;

import com.classiverse.backend.domain.story.entity.Story;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StoryRepository extends JpaRepository<Story, Long> {

    // "Book" 객체의 "BookId"가 일치하는 것을 찾아서 "EpisodeNum" 기준으로 "Asc(오름차순)" 정렬해라
    List<Story> findAllByBook_BookIdOrderByEpisodeNumAsc(Long bookId);
}