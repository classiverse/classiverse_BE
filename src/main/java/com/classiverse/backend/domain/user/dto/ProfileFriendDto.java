package com.classiverse.backend.domain.user.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ProfileFriendDto {

    private final Long charId;
    private final String name;
    private final Integer closeness;
    private final Long bookId;
    private final String bookTitle;
    private final String categoryName;
}