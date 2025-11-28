package com.classiverse.backend.domain.unlock.repository;

import com.classiverse.backend.domain.category.entity.Category;
import com.classiverse.backend.domain.unlock.entity.UserCategoryUnlock;
import com.classiverse.backend.domain.unlock.entity.UserCategoryUnlockId;
import com.classiverse.backend.domain.user.entity.User;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserCategoryUnlockRepository
        extends JpaRepository<UserCategoryUnlock, UserCategoryUnlockId> {

    // 특정 유저가 가진 성운 unlock 상태
    List<UserCategoryUnlock> findByUser(User user);

    // 유저+카테고리 한 건 조회
    Optional<UserCategoryUnlock> findByUserAndCategory(User user, Category category);
}