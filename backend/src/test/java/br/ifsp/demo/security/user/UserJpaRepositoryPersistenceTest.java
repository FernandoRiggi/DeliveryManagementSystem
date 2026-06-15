package br.ifsp.demo.security.user;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@Tag("PersistenceTest")
@Tag("IntegrationTest")
class UserJpaRepositoryPersistenceTest {

    @Autowired
    private JpaUserRepository userRepository;

    private final List<UUID> createdUserIds = new ArrayList<>();

    @AfterEach
    void cleanUsers() {
        createdUserIds.forEach(userRepository::deleteById);
        createdUserIds.clear();
    }

    @Test
    @DisplayName("save should fail when email is duplicated")
    void saveShouldFailWhenEmailIsDuplicated() {
        String email = "persistence-test-" + UUID.randomUUID() + "@example.com";
        User firstUser = user(email);
        User duplicatedEmailUser = user(email);
        createdUserIds.add(firstUser.getId());
        createdUserIds.add(duplicatedEmailUser.getId());

        userRepository.saveAndFlush(firstUser);

        assertThatThrownBy(() -> userRepository.saveAndFlush(duplicatedEmailUser))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("email");
    }

    private User user(String email) {
        return User.builder()
                .id(UUID.randomUUID())
                .name("Persistence")
                .lastname("Test")
                .email(email)
                .password("encrypted-password")
                .role(Role.USER)
                .build();
    }
}
