package org.example;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class AppTest {

    @Test
    void getGreeting() {
        App app = new App();
        assertThat(app.getGreeting()).isEqualTo("Hello World!");
    }
}