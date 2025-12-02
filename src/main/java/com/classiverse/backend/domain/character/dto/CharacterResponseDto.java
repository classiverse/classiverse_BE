package com.classiverse.backend.domain.character.dto;

import com.classiverse.backend.domain.character.entity.StoryCharacter;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class CharacterResponseDto {
    private Long charId;
    private String name;
    private Integer closeness;
    private String charImage;

    public CharacterResponseDto(StoryCharacter character, Integer closeness) {
        this.charId = character.getCharId();
        this.name = character.getName();
        this.closeness = closeness != null ? closeness : 0; // default 0%
        this.charImage = character.getCharImage();
    }
}