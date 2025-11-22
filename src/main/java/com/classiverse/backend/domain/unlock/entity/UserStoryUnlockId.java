package com.classiverse.backend.domain.unlock.entity;

import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserStoryUnlockId implements Serializable {
    private Long user;
    private Long story;
}