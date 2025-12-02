package com.classiverse.backend.domain.user.service;

import com.classiverse.backend.domain.book.entity.Book;
import com.classiverse.backend.domain.category.entity.Category;
import com.classiverse.backend.domain.character.dto.CharacterResponseDto;
import com.classiverse.backend.domain.character.entity.StoryCharacter;
import com.classiverse.backend.domain.closeness.entity.Closeness;
import com.classiverse.backend.domain.closeness.repository.ClosenessRepository;
import com.classiverse.backend.domain.user.dto.ProfileFriendDto;
import com.classiverse.backend.domain.user.dto.ProfileMeResponseDto;
import com.classiverse.backend.domain.user.entity.User;
import com.classiverse.backend.domain.user.exception.DuplicateNicknameException;
import com.classiverse.backend.domain.user.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProfileService {

    private final UserRepository userRepository;
    private final ClosenessRepository closenessRepository;

    public ProfileMeResponseDto getMyProfile(Long userId) {
        // 1) 유저 검증
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("존재하지 않는 사용자입니다. id=" + userId));

        // 2) 친밀도 > 0인 캐릭터 목록
        List<Closeness> closenessList =
                closenessRepository.findWithAllRelationsByUserAndClosenessGreaterThan(user, 0);

        // 3) 엔티티 → DTO 매핑 (프로필 요약용)
        List<ProfileFriendDto> friends = closenessList.stream()
                .map(c -> {
                    StoryCharacter character = c.getCharacter();
                    Book book = character.getBook();
                    Category category = book.getCategory();

                    return new ProfileFriendDto(
                            character.getCharId(),
                            character.getName(),
                            c.getCloseness(),
                            book.getBookId(),
                            book.getTitle(),
                            category != null ? category.getCategoryName() : null
                    );
                })
                .toList();

        return new ProfileMeResponseDto(
                user.getNickname(),
                user.getProfileImage(), // 추가된 부분!
                friends
        );
    }

    // CharacterResponseDto(charId, name, closeness) 재사용

    public List<CharacterResponseDto> getMyCharacters(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("존재하지 않는 사용자입니다. id=" + userId));

        List<Closeness> closenessList =
                closenessRepository.findWithAllRelationsByUserAndClosenessGreaterThan(user, 0);

        return closenessList.stream()
                .map(c -> new CharacterResponseDto(c.getCharacter(), c.getCloseness()))
                .toList();
    }

    @Transactional
    public void updateNickname(Long userId, String newNickname) {
        if (newNickname == null || newNickname.isBlank()) {
            throw new IllegalArgumentException("닉네임은 비워 둘 수 없습니다.");
        }
        if (newNickname.length() > 20) {
            throw new IllegalArgumentException("닉네임은 최대 20자까지 가능합니다.");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("존재하지 않는 사용자입니다. id=" + userId));

        // 동일 닉네임으로 바꾸는 건 허용
        if (!newNickname.equals(user.getNickname())
                && userRepository.existsByNickname(newNickname)) {
            throw new DuplicateNicknameException("이미 사용 중인 닉네임입니다.");
        }

        user.changeNickname(newNickname);
    }
}