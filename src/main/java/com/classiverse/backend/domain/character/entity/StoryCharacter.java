package com.classiverse.backend.domain.character.entity;

import com.classiverse.backend.domain.book.entity.Book;
import com.classiverse.backend.domain.common.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "story_character")
public class StoryCharacter extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "char_id")
    private Long charId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "book_id", nullable = false)
    private Book book;

    @Column(nullable = false)
    private String name;

    private String intro;

    private String info;

    public StoryCharacter(Book book, String name, String intro, String info) {
        this.book = book;
        this.name = name;
        this.intro = intro;
        this.info = info;
    }
}