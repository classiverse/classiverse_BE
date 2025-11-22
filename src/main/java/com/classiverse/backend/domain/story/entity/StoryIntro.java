package com.classiverse.backend.domain.story.entity;

import com.classiverse.backend.domain.character.entity.StoryCharacter; // 캐릭터 import 확인
import com.classiverse.backend.domain.common.BaseTimeEntity;
import com.classiverse.backend.domain.common.YnType;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "story_intro")
public class StoryIntro extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "intro_id")
    private Long introId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "story_id", nullable = false)
    private Story story;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "char_id", nullable = false)
    private StoryCharacter character;

    @Column(name = "intro_text", nullable = false)
    private String introText;

    @Enumerated(EnumType.STRING)
    @Column(name = "is_leader", nullable = false)
    private YnType isLeader = YnType.N;

    // 생성자
    public StoryIntro(Story story, StoryCharacter character, String introText, YnType isLeader) {
        this.story = story;
        this.character = character;
        this.introText = introText;
        this.isLeader = isLeader;
    }
}