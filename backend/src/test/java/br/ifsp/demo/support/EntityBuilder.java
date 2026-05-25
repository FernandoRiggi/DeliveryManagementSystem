package br.ifsp.demo.support;

import br.ifsp.demo.security.auth.RegisterUserRequest;
import br.ifsp.demo.security.user.Role;
import br.ifsp.demo.security.user.User;
import com.github.javafaker.Faker;

import java.util.UUID;

public class EntityBuilder {
    private static final Faker faker = Faker.instance();
    public static final String TEST_EMAIL_PREFIX = "api-test-";

    private EntityBuilder() {
    }

    public static RegisterUserRequest createRandomRegisterUserRequest(String password) {
        return new RegisterUserRequest(
                faker.name().firstName(),
                faker.name().lastName(),
                uniqueEmail(),
                password
        );
    }

    public static User createRandomUser(String encryptedPassword) {
        return User.builder()
                .id(UUID.randomUUID())
                .name(faker.name().firstName())
                .lastname(faker.name().lastName())
                .email(uniqueEmail())
                .password(encryptedPassword)
                .role(Role.USER)
                .build();
    }

    private static String uniqueEmail() {
        return TEST_EMAIL_PREFIX + UUID.randomUUID() + "@" + faker.internet().domainName();
    }
}
