package com.classiverse.backend.domain.category.repository;

import com.classiverse.backend.domain.category.entity.Category;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<Category, Long> {

    // 성운(카테고리) 목록을 ID 순서대로 반환
    List<Category> findAllByOrderByCategoryIdAsc();
}