package com.classiverse.backend.domain.unlock.entity;

import com.classiverse.backend.domain.category.entity.Category;
import com.classiverse.backend.domain.common.BaseTimeEntity;
import com.classiverse.backend.domain.common.YnType;
import com.classiverse.backend.domain.user.entity.User;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@IdClass(UserCategoryUnlockId.class)
@Table(name = "user_category_unlock")
public class UserCategoryUnlock extends BaseTimeEntity {

    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private Category category;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private YnType unlocked = YnType.N;

    private LocalDateTime unlockedAt;

    public UserCategoryUnlock(User user, Category category, YnType unlocked) {
        this.user = user;
        this.category = category;
        this.unlocked = unlocked;
        if (unlocked == YnType.Y) {
            this.unlockedAt = LocalDateTime.now();
        }
    }
}