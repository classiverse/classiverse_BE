package com.classiverse.backend.domain.story.entity;

import com.classiverse.backend.domain.book.entity.Book;
import com.classiverse.backend.domain.common.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "story")
public class Story extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "story_id")
    private Long storyId;

    // FK: Book과 연결
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "book_id", nullable = false)
    private Book book;

    @Column(name = "episode_num", nullable = false)
    private Integer episodeNum;

    @Column(nullable = false)
    private String title;

    public Story(Book book, Integer episodeNum, String title) {
        this.book = book;
        this.episodeNum = episodeNum;
        this.title = title;
    }
}