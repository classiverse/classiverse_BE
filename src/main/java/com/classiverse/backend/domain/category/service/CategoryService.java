package com.classiverse.backend.domain.category.service;

import com.classiverse.backend.domain.category.dto.CategoryMeResponseDto;
import com.classiverse.backend.domain.category.entity.Category;
import com.classiverse.backend.domain.category.repository.CategoryRepository;
import com.classiverse.backend.domain.closeness.repository.ClosenessRepository;
import com.classiverse.backend.domain.common.YnType;
import com.classiverse.backend.domain.unlock.entity.UserCategoryUnlock;
import com.classiverse.backend.domain.unlock.repository.UserCategoryUnlockRepository;
import com.classiverse.backend.domain.user.entity.User;
import com.classiverse.backend.domain.user.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;
    private final UserCategoryUnlockRepository userCategoryUnlockRepository;
    private final ClosenessRepository closenessRepository;

    public List<CategoryMeResponseDto> getMyCategories(Long userId) {
        // 1) 유저 조회
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("존재하지 않는 사용자입니다. id=" + userId));

        // 2) 전체 카테고리 목록
        List<Category> categories = categoryRepository.findAllByOrderByCategoryIdAsc();

        // 3) 유저가 가진 unlock 정보 한 번에 조회해서 Map으로 변환
        List<UserCategoryUnlock> unlocks = userCategoryUnlockRepository.findByUser(user);
        Map<Long, UserCategoryUnlock> unlockMap = unlocks.stream()
                .collect(Collectors.toMap(
                        u -> u.getCategory().getCategoryId(),
                        Function.identity()
                ));

        // 4) 카테고리별 상태 + 최대 친밀도 조합해서 DTO 리스트 반환
        return categories.stream()
                .map(category -> {
                    UserCategoryUnlock unlock = unlockMap.get(category.getCategoryId());
                    boolean unlocked = unlock != null && unlock.getUnlocked() == YnType.Y;
                    LocalDateTime unlockedAt = unlock != null ? unlock.getUnlockedAt() : null;

                    Integer maxCloseness =
                            closenessRepository.findMaxClosenessByUserAndCategory(user, category);
                    if (maxCloseness == null) {
                        maxCloseness = 0;
                    }

                    return new CategoryMeResponseDto(
                            category.getCategoryId(),
                            category.getCategoryName(),
                            unlocked,
                            unlockedAt,
                            maxCloseness
                    );
                })
                .toList();
    }
}