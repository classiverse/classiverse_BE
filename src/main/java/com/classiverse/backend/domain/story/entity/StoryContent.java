package com.classiverse.backend.domain.story.entity;

import com.classiverse.backend.domain.character.entity.StoryCharacter;
import com.classiverse.backend.domain.common.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "story_content")
public class StoryContent extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "content_id")
    private Long contentId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "story_id", nullable = false)
    private Story story;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "char_id", nullable = false)
    private StoryCharacter character;

    @Column(nullable = false)
    private Integer seq;

    @Column(length = 255)
    private String header;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String content;

    // 리액션 버튼 텍스트
    private String reaction1;
    private String reaction2;

    @Column(name = "next_content_id_1")
    private Long nextContentId1;

    @Column(name = "next_content_id_2")
    private Long nextContentId2;
}