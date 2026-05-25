package br.ifsp.demo.support;

import br.ifsp.demo.security.auth.AuthRequest;
import br.ifsp.demo.security.user.JpaUserRepository;
import br.ifsp.demo.security.user.User;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public abstract class BaseApiIntegrationTest {

    @LocalServerPort
    protected int port;

    @Autowired
    protected JpaUserRepository userRepository;

    @BeforeEach
    void generalSetup() {
        RestAssured.baseURI = "http://localhost";
        RestAssured.port = port;
    }

    @AfterEach
    void tearDown() {
        userRepository.findAll().stream()
                .filter(user -> user.getEmail().startsWith(EntityBuilder.TEST_EMAIL_PREFIX))
                .forEach(userRepository::delete);
    }

    protected User registerUser(String plainTextPassword) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        User user = EntityBuilder.createRandomUser(encoder.encode(plainTextPassword));
        return userRepository.save(user);
    }

    protected String authenticate(String email, String password) {
        AuthRequest authRequest = new AuthRequest(email, password);

        return RestAssured
                .given()
                .contentType(ContentType.JSON)
                .body(authRequest)
                .when()
                .post("/api/v1/authenticate")
                .then()
                .statusCode(200)
                .extract()
                .path("token");
    }
}
