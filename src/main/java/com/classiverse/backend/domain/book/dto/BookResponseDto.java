package com.classiverse.backend.domain.book.dto;

import com.classiverse.backend.domain.book.entity.Book;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Getter
@NoArgsConstructor
public class BookResponseDto {
    private Long bookId;
    private String title;
    private String author;
    private String introduction;
    private String categoryName;
    private String planetImage;
    private List<String> bookImages;

    public BookResponseDto(Book book) {
        this.bookId = book.getBookId();
        this.title = book.getTitle();
        this.author = book.getAuthor();
        this.introduction = book.getIntroduction();

        if (book.getCategory() != null) {
            this.categoryName = book.getCategory().getCategoryName();
        } else {
            this.categoryName = "카테고리 없음";
        }

        this.planetImage = book.getPlanetImage();

        // ★ [핵심 수정] 리스트를 "새로운 ArrayList"로 복사해서 끊어내야 합니다!
        // 이렇게 하면 트랜잭션 안에서 데이터를 싹 가져와서 담아둡니다.
        this.bookImages = (book.getBookImages() != null)
                ? new ArrayList<>(book.getBookImages())
                : new ArrayList<>();
    }
}