package com.classiverse.backend.domain.book.controller;

import com.classiverse.backend.domain.book.dto.BookResponseDto;
import com.classiverse.backend.domain.book.service.BookService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/books")
public class BookController {

    private final BookService bookService;

    // 책 기본 정보 조회 API
    // 책기본정보:제목, 저자, 소개, 카테고리명, 성운정보 불러오기
    // GET /api/books/{bookId}
    @GetMapping("/{bookId}")
    public ResponseEntity<BookResponseDto> getBookBasicInfo(@PathVariable Long bookId) {
        BookResponseDto response = bookService.getBookInfo(bookId);
        return ResponseEntity.ok(response);
    }
}