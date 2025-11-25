package com.classiverse.backend.domain.book.service;

import com.classiverse.backend.domain.book.dto.BookResponseDto;
import com.classiverse.backend.domain.book.entity.Book;
import com.classiverse.backend.domain.book.repository.BookRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class BookService {

    private final BookRepository bookRepository;

    @Transactional(readOnly = true)
    public BookResponseDto getBookInfo(Long bookId) {
        // 1. DB에서 책 조회 (없으면 예외 발생)
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new IllegalArgumentException("해당 책을 찾을 수 없습니다. id=" + bookId));

        // 2. DTO로 변환하여 반환
        return new BookResponseDto(book);
    }
}