package com.acme.salary.web;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.web.servlet.assertj.MockMvcTester;

@WebMvcTest(SpaForwardingController.class)
class SpaForwardingControllerTest {

    @Autowired
    private MockMvcTester mvc;

    @ParameterizedTest
    @ValueSource(strings = {"/employees", "/insights", "/insights/IN"})
    void servesTheAngularAppForItsRoutes(String route) {
        assertThat(mvc.get().uri(route)).hasForwardedUrl("/index.html");
    }
}
