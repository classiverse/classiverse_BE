package com.classiverse.backend.domain.book.entity;

import com.classiverse.backend.domain.category.entity.Category;
import com.classiverse.backend.domain.common.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "book")
public class Book extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "book_id")
    private Long bookId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    @Column(nullable = false)
    private String title;

    private String author;

    @Column(columnDefinition = "TEXT")
    private String introduction;

    // 행성 이미지 URL (썸네일)
    @Column(name = "planet_image", length = 500)
    private String planetImage;

    // ★ [수정] 기존 bookImage(단일) 삭제 -> 리스트(여러 개)로 변경
    // 별도의 테이블(book_images)에 저장됩니다.
    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = "book_images", joinColumns = @JoinColumn(name = "book_id"))
    @Column(name = "image_url", length = 500)
    private List<String> bookImages = new ArrayList<>();

    public Book(Category category, String title, String author, String introduction, String planetImage, List<String> bookImages) {
        this.category = category;
        this.title = title;
        this.author = author;
        this.introduction = introduction;
        this.planetImage = planetImage;
        this.bookImages = bookImages;
    }
}