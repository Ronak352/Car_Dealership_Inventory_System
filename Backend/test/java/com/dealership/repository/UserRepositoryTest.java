package com.dealership.repository;

import com.dealership.entity.User;
import com.dealership.enums.Role;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * RED PHASE: fails to compile until UserRepository exists under
 * com.dealership.repository.
 */
class UserRepositoryTest extends AbstractRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TestEntityManager entityManager;

    @Test
    void findByEmail_returnsUser_whenEmailExists() {
        User user = User.builder()
                .firstName("Ronak").lastName("Patel")
                .email("ronak@example.com")
                .password("hashed-pw")
                .role(Role.ADMIN)
                .build();
        entityManager.persistAndFlush(user);

        Optional<User> found = userRepository.findByEmail("ronak@example.com");

        assertThat(found).isPresent();
        assertThat(found.get().getFirstName()).isEqualTo("Ronak");
    }

    @Test
    void findByEmail_returnsEmpty_whenEmailDoesNotExist() {
        Optional<User> found = userRepository.findByEmail("missing@example.com");

        assertThat(found).isEmpty();
    }

    @Test
    void existsByEmail_returnsTrue_whenDuplicate() {
        User user = User.builder()
                .firstName("Asha").lastName("Shah")
                .email("asha@example.com")
                .password("hashed-pw")
                .role(Role.CUSTOMER)
                .build();
        entityManager.persistAndFlush(user);

        assertThat(userRepository.existsByEmail("asha@example.com")).isTrue();
        assertThat(userRepository.existsByEmail("nobody@example.com")).isFalse();
    }
}
