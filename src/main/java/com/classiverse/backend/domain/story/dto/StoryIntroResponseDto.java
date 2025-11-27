package com.classiverse.backend.domain.story.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
public class StoryIntroResponseDto {
    private String storyTitle;
    private List<CharacterIntroDto> introductions;

    public StoryIntroResponseDto(String storyTitle, List<CharacterIntroDto> introductions) {
        this.storyTitle = storyTitle;
        this.introductions = introductions;
    }
}