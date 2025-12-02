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
    private String header;
    private String content;
    private String charVideo;

    // 버튼 정보 (텍스트 + 이동할 ID)
    private List<ReactionDto> reactions;

    @Getter
    public static class ReactionDto {
        private String text;   // 버튼에 뜰 글자
        private Long nextId;   // 이동할 ID

        public ReactionDto(String text, Long nextId) {
            this.text = text;
            this.nextId = nextId;
        }
    }

    public StoryContentResponseDto(StoryContent storyContent) {
        this.storyTitle = storyContent.getStory().getTitle();
        this.charId = storyContent.getCharacter().getCharId();
        this.characterName = storyContent.getCharacter().getName();
        this.header = storyContent.getHeader();
        this.content = storyContent.getContent();
        this.charVideo = storyContent.getCharacter().getCharVideo();


        this.reactions = new ArrayList<>();

        if (storyContent.getReaction1() != null) {
            this.reactions.add(new ReactionDto(storyContent.getReaction1(), storyContent.getNextContentId1()));
        }

        if (storyContent.getReaction2() != null) {
            this.reactions.add(new ReactionDto(storyContent.getReaction2(), storyContent.getNextContentId2()));
        }
    }
}