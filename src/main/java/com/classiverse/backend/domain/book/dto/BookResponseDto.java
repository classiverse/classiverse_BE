package com.classiverse.backend.domain.book.dto;

import com.classiverse.backend.domain.book.entity.Book;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class BookResponseDto {
    private Long bookId;
    private String title;
    private String author;
    private String introduction;
    private String categoryName;

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
    }
}