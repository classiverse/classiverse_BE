package com.classiverse.backend.domain.reading.entity;

import com.classiverse.backend.domain.character.entity.StoryCharacter;
import com.classiverse.backend.domain.common.BaseTimeEntity;
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
@IdClass(ReadingProgressId.class)
@Table(name = "reading_progress")
public class ReadingProgress extends BaseTimeEntity {

    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "story_id")
    private Story story;

    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "char_id")
    private StoryCharacter character;
    private LocalDateTime finishedAt;

    public ReadingProgress(User user, Story story, StoryCharacter character, LocalDateTime finishedAt) {
        this.user = user;
        this.story = story;
        this.character = character;
        this.finishedAt = finishedAt;
    }
}