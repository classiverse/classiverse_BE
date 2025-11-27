package com.classiverse.backend.domain.unlock.entity;

import com.classiverse.backend.domain.common.BaseTimeEntity;
import com.classiverse.backend.domain.common.YnType;
import com.classiverse.backend.domain.story.entity.Story;
import com.classiverse.backend.domain.user.entity.User;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@IdClass(UserStoryUnlockId.class)
@Table(name = "user_story_unlock")
public class UserStoryUnlock extends BaseTimeEntity {

    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "story_id")
    private Story story;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private YnType unlocked = YnType.N;

    private LocalDateTime unlockedAt;

    public UserStoryUnlock(User user, Story story, YnType unlocked) {
        this.user = user;
        this.story = story;
        this.unlocked = unlocked;
        if (unlocked == YnType.Y) {
            this.unlockedAt = LocalDateTime.now();
        }
    }
}