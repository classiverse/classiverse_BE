package com.classiverse.backend.domain.story.entity;

import com.classiverse.backend.domain.character.entity.StoryCharacter;
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

    @Column(name = "final_text", nullable = false) // 기존 데이터가 있다면 에러 날 수 있으므로 주의 (운영 중이면 nullable=true 권장)
    private String finalText;

    @Enumerated(EnumType.STRING)
    @Column(name = "is_leader", nullable = false)
    private YnType isLeader = YnType.N;

    public StoryIntro(Story story, StoryCharacter character, String introText, String finalText, YnType isLeader) {
        this.story = story;
        this.character = character;
        this.introText = introText;
        this.finalText = finalText; // 추가
        this.isLeader = isLeader;
    }
}