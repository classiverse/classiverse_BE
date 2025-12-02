package com.classiverse.backend.domain.character.dto;

import com.classiverse.backend.domain.character.entity.StoryCharacter;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class CharacterDetailResponseDto {
    private Long charId;
    private String name;
    private Integer closeness; // 친밀도
    private String intro;      // 한줄 소개
    private String info;       // 상세 정보
    private String charImage;

    public CharacterDetailResponseDto(StoryCharacter character, Integer closeness) {
        this.charId = character.getCharId();
        this.name = character.getName();
        this.intro = character.getIntro();
        this.info = character.getInfo();
        this.closeness = closeness != null ? closeness : 0;
        this.charImage = character.getCharImage();
    }
}