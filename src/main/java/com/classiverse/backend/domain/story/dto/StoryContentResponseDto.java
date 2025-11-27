package com.classiverse.backend.domain.story.dto;

import com.classiverse.backend.domain.story.entity.StoryContent;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Getter
@NoArgsConstructor
public class StoryContentResponseDto {
    private String storyTitle;
    private Long charId;
    private String characterName;
    private String header;   // 강조 문구
    private String content;  // 본문
    private List<String> reactions; // 버튼 텍스트 리스트

    // 다음 페이지 ID
    private Long nextContentId;

    public StoryContentResponseDto(StoryContent storyContent, Long nextContentId) {
        this.storyTitle = storyContent.getStory().getTitle();
        this.charId = storyContent.getCharacter().getCharId();
        this.characterName = storyContent.getCharacter().getName();
        this.header = storyContent.getHeader();
        this.content = storyContent.getContent();

        this.nextContentId = nextContentId;

        // 리액션 버튼 텍스트 담기
        this.reactions = new ArrayList<>();
        if (storyContent.getReaction1() != null) {
            this.reactions.add(storyContent.getReaction1());
        }
        if (storyContent.getReaction2() != null) {
            this.reactions.add(storyContent.getReaction2());
        }
    }
}