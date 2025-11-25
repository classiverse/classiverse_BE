package com.classiverse.backend.domain.closeness.repository;

import com.classiverse.backend.domain.closeness.entity.Closeness;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ClosenessRepository extends JpaRepository<Closeness, Long> { // ID 타입은 복합키라 수정 필요할 수도 있지만, 조회만 할 거면 일단 둡니다.

    // 해석: 특정 유저(UserId)가 특정 책(BookId)에 속한 캐릭터와 맺은 관계들을 다 찾아줘
    // Closeness -> Character -> Book 으로 연결해서 찾습니다.
    List<Closeness> findByUser_UserIdAndCharacter_Book_BookId(Long userId, Long bookId);
}