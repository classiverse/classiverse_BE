package com.classiverse.backend.domain.story.dto;

import com.classiverse.backend.domain.common.YnType;
import com.classiverse.backend.domain.story.entity.StoryIntro;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class CharacterIntroDto {
    private Long charId;
    private String characterName;
    private String introText;
    private YnType isLeader; // "Y" or "N"

    public CharacterIntroDto(StoryIntro storyIntro) {
        this.charId = storyIntro.getCharacter().getCharId();
        this.characterName = storyIntro.getCharacter().getName();
        this.introText = storyIntro.getIntroText();
        this.isLeader = storyIntro.getIsLeader();
    }
}