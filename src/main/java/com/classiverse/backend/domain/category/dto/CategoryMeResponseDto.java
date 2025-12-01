package com.classiverse.backend.domain.category.dto;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CategoryMeResponseDto {

    private final Long categoryId;
    private final String categoryName;
    private final boolean unlocked;
    private final LocalDateTime unlockedAt;
    private final Integer maxClosenessInCategory;
}