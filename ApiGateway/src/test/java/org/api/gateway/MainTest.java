package org.api.gateway;

import com.github.tomakehurst.wiremock.matching.RegexPattern;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.test.web.reactive.server.WebTestClient;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static com.github.tomakehurst.wiremock.client.WireMock.get;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
    properties = {"httpbin=http://localhost:${wiremock.server.port}"})
@AutoConfigureWireMock(port = 0)
public class MainTest {

    @Autowired
    private WebTestClient webClient;

    @Value("${wiremock.server.port}")
    private int port;

    /**
     * Run with ApiUsers microservice running
     */
    @Test
    public void apiUsersContextLoads() {
        stubFor(get(urlEqualTo("/apiusers/users/status"))
            .withHeader("Authorization", new RegexPattern("Bearer (.*)"))
            .willReturn(aResponse()
                .withBody("Running on port ")));

        webClient
            .get().uri("/apiusers/users/status")
            .header("Authorization", "Bearer eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJqaGFzZDFAYWtzZC5jb20ifQ.xBdImiyttyzu7yu75RD_aKjOoLu9hjKT29B9TwDiUgU")
            .exchange()
            .expectStatus().isOk()
            .expectBody();
    }
}
