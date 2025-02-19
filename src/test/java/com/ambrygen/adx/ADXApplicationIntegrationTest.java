package com.ambrygen.adx;

import com.ambrygen.adx.dto.security.JwtResponse;
import com.ambrygen.adx.dto.security.LoginRequest;
import com.ambrygen.adx.dto.security.SigninResponseDTO;
import org.junit.jupiter.api.Assertions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.client.RestTemplate;
import org.testcontainers.containers.MySQLContainer;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@TestPropertySource(locations = {"classpath:application-test.properties"})
@DirtiesContext
public abstract class ADXApplicationIntegrationTest {
    @Autowired
    protected WebTestClient webClient;
    private RestTemplate restTemplate;
    protected static final MySQLContainer mySQLContainer;

    static {
        mySQLContainer = new MySQLContainer("mysql:latest");
        mySQLContainer.start();
    }

    protected static String jwt = null;

    public ADXApplicationIntegrationTest(RestTemplateBuilder builder) {
        this.restTemplate = builder.build();
    }

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", mySQLContainer::getJdbcUrl);
        registry.add("spring.datasource.username", mySQLContainer::getUsername);
        registry.add("spring.datasource.password", mySQLContainer::getPassword);
    }

    protected void login(String userName, String pwd) {
        String signinUrl = "http://localhost:8888/api/auth/signin";
        LoginRequest loginRequest = new LoginRequest(userName, pwd);
        HttpEntity<LoginRequest> request = new HttpEntity<>(loginRequest);
        SigninResponseDTO signinResponseDTO =
                this.restTemplate.postForObject(signinUrl, request, SigninResponseDTO.class);
        Assertions.assertNotNull(signinResponseDTO);
        this.jwt = ((JwtResponse) signinResponseDTO.getResponse()).getToken();
    }
}
