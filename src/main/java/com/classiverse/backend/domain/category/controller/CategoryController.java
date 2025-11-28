package com.classiverse.backend.domain.category.controller;

import com.classiverse.backend.domain.category.dto.CategoryMeResponseDto;
import com.classiverse.backend.domain.category.service.CategoryService;
import com.classiverse.backend.domain.user.security.CustomUserPrincipal;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    @GetMapping("/me")
    public List<CategoryMeResponseDto> getMyCategories(@AuthenticationPrincipal CustomUserPrincipal principal) {
        Long userId = principal.getUserId(); // ★ 프로젝트에서 사용하는 Principal 타입에 맞게 수정
        return categoryService.getMyCategories(userId);
    }
}