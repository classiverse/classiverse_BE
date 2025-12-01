package com.classiverse.backend.domain.user.dto;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ProfileMeResponseDto {

    private final String nickname;
    private final List<ProfileFriendDto> friends;
}