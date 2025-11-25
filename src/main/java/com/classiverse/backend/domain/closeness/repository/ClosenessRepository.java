package com.classiverse.backend.domain.closeness.repository;

import com.classiverse.backend.domain.closeness.entity.Closeness;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ClosenessRepository extends JpaRepository<Closeness, Long> {

    // Closeness -> Character -> Book 으로 연결해서 찾
    List<Closeness> findByUser_UserIdAndCharacter_Book_BookId(Long userId, Long bookId);
    // 단건 조회용: 내(User)가 이 캐릭터(Character)랑 얼마나 친한지
    Optional<Closeness> findByUser_UserIdAndCharacter_CharId(Long userId, Long charId);
}