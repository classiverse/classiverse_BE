package com.classiverse.backend.domain.category.controller;

import com.classiverse.backend.domain.book.dto.BookResponseDto;
import com.classiverse.backend.domain.book.service.BookService;
import com.classiverse.backend.domain.category.dto.CategoryMeResponseDto;
import com.classiverse.backend.domain.category.service.CategoryService;
import com.classiverse.backend.domain.user.security.CustomUserPrincipal;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;
    private final BookService bookService;

    // 사용자별 해금된 카테고리 목록
    @GetMapping("/me")
    public List<CategoryMeResponseDto> getMyCategories(@AuthenticationPrincipal CustomUserPrincipal principal) {
        Long userId = principal.getUserId();
        return categoryService.getMyCategories(userId);
    }

    // 카테고리(성운)별 책 목록 조회
    @GetMapping("/{categoryId}/books")
    public List<BookResponseDto> getBooksByCategory(@PathVariable Long categoryId) {
        return bookService.getBooksByCategory(categoryId);
    }
}