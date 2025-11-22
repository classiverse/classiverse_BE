package com.classiverse.backend.domain.book.entity;

import com.classiverse.backend.domain.category.entity.Category;
import com.classiverse.backend.domain.common.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "book")
public class Book extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "book_id")
    private Long bookId;

    // FK: Category와 연결
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    @Column(nullable = false)
    private String title;

    private String author;

    @Column(columnDefinition = "TEXT")
    private String introduction;

    public Book(Category category, String title, String author, String introduction) {
        this.category = category;
        this.title = title;
        this.author = author;
        this.introduction = introduction;
    }
}