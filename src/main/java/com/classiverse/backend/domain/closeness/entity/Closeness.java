package com.classiverse.backend.domain.closeness.entity;

import com.classiverse.backend.domain.character.entity.StoryCharacter;
import com.classiverse.backend.domain.common.BaseTimeEntity;
import com.classiverse.backend.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@IdClass(ClosenessId.class)
@Table(name = "closeness")
public class Closeness extends BaseTimeEntity {

    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "char_id")
    private StoryCharacter character;

    @Column(nullable = false)
    private Integer closeness = 0;
}