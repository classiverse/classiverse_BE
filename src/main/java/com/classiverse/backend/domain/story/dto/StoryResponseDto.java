package com.classiverse.backend.domain.story.dto;

import com.classiverse.backend.domain.story.entity.Story;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class StoryResponseDto {
    private Long storyId;
    private Integer episodeNum;
    private String title;
    private Boolean isLocked;

    public StoryResponseDto(Story story, Boolean isLocked) {
        this.storyId = story.getStoryId();
        this.episodeNum = story.getEpisodeNum();
        this.title = story.getTitle();
        this.isLocked = isLocked;
    }
}