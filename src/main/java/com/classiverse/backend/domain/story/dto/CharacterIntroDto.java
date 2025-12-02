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
    private YnType isLeader;
    private String charImage;
    private Long firstContentId;

    public CharacterIntroDto(StoryIntro storyIntro, Long firstContentId) {
        this.charId = storyIntro.getCharacter().getCharId();
        this.characterName = storyIntro.getCharacter().getName();
        this.introText = storyIntro.getIntroText();
        this.isLeader = storyIntro.getIsLeader();
        this.charImage = storyIntro.getCharacter().getCharImage();

        // ★ [추가]
        this.firstContentId = firstContentId;
    }
}