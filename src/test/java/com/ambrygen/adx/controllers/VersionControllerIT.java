package com.ambrygen.adx.controllers;

import com.ambrygen.adx.ADXApplicationIntegrationTest;
import com.ambrygen.adx.dto.VersionInfo;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.test.web.reactive.server.EntityExchangeResult;

import static org.assertj.core.api.Assertions.assertThat;

public class VersionControllerIT extends ADXApplicationIntegrationTest {
    @Autowired
    public VersionControllerIT(RestTemplateBuilder builder) {
        super(builder);
    }

    @Test
    public void versionIsAvailable() {
        EntityExchangeResult<VersionInfo> result = webClient.get().uri("/version")
                .exchange()
                .expectStatus().isOk()
                .expectBody(VersionInfo.class)
                .returnResult();

        VersionInfo versionInfo = result.getResponseBody();
        assertThat(versionInfo).isNotNull();
        assertThat(versionInfo.getDescription()).isEqualTo("ADX REST APIs");
        assertThat(versionInfo.getName()).isEqualTo("ADX REST APIs");
    }
}
