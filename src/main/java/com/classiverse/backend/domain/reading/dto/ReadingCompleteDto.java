package com.classiverse.backend.domain.reading.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ReadingCompleteDto {
    private String storyTitle;      // 스토리 제목
    private String characterName;   // 캐릭터 이름
    private Integer currentCloseness; // 현재 친밀도
    private String finalText;       // 캐릭터의 마지막 한마디
    private String charImage;
}