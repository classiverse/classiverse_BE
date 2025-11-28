package com.classiverse.backend.domain.closeness.repository;

import com.classiverse.backend.domain.category.entity.Category;
import com.classiverse.backend.domain.closeness.entity.Closeness;
import com.classiverse.backend.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ClosenessRepository extends JpaRepository<Closeness, Long> {

    // Closeness -> Character -> Book 으로 연결해서 찾기
    List<Closeness> findByUser_UserIdAndCharacter_Book_BookId(Long userId, Long bookId);

    // 단건 조회용: 내(User)가 이 캐릭터(Character)랑 얼마나 친한지
    Optional<Closeness> findByUser_UserIdAndCharacter_CharId(Long userId, Long charId);

    // 프로필 화면용: 친밀도 > 0 인 캐릭터들을 점수 내림차순으로
    List<Closeness> findByUserAndClosenessGreaterThanOrderByClosenessDesc(User user, int closeness);

    // 카테고리별 최대 친밀도
    @Query("""
        select max(c.closeness)
        from Closeness c
        where c.user = :user
          and c.character.book.category = :category
        """
    )
    Integer findMaxClosenessByUserAndCategory(
            @Param("user") User user,
            @Param("category") Category category
    );

    @Query("""
        select c
        from Closeness c
        join fetch c.character ch
        join fetch ch.book b
        left join fetch b.category cat
        where c.user = :user
          and c.closeness > :min
        order by c.closeness desc
        """
    )
    List<Closeness> findWithAllRelationsByUserAndClosenessGreaterThan(
            @Param("user") User user,
            @Param("min") int min
    );
}