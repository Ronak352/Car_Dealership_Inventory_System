package com.dealership.repository;

import com.dealership.entity.RefreshToken;
import com.dealership.entity.User;
import com.dealership.enums.Role;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

/** RED PHASE: fails to compile until RefreshTokenRepository exists. */
class RefreshTokenRepositoryTest extends AbstractRepositoryTest {

    @Autowired
    private RefreshTokenRepository refreshTokenRepository;

    @Autowired
    private TestEntityManager entityManager;

    @Test
    void findByToken_returnsMatchingRefreshToken() {
        User user = entityManager.persistAndFlush(User.builder()
                .firstName("Isha").lastName("Kapoor").email("isha@example.com")
                .password("pw").role(Role.CUSTOMER).build());

        RefreshToken refreshToken = RefreshToken.builder()
                .user(user).token("refresh-abc-123")
                .expiryDate(LocalDateTime.now().plusDays(7))
                .build();
        entityManager.persistAndFlush(refreshToken);

        Optional<RefreshToken> found = refreshTokenRepository.findByToken("refresh-abc-123");

        assertThat(found).isPresent();
        assertThat(found.get().getUser().getEmail()).isEqualTo("isha@example.com");
    }

    @Test
    void deleteByUserId_removesAllTokensForUser() {
        User user = entityManager.persistAndFlush(User.builder()
                .firstName("Yash").lastName("Trivedi").email("yash@example.com")
                .password("pw").role(Role.CUSTOMER).build());

        entityManager.persistAndFlush(RefreshToken.builder()
                .user(user).token("token-1")
                .expiryDate(LocalDateTime.now().plusDays(7)).build());

        refreshTokenRepository.deleteByUserId(user.getId());
        entityManager.flush();

        assertThat(refreshTokenRepository.findByToken("token-1")).isEmpty();
    }
}
