package org.example;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
class AppTest {

    @Test
    void getGreeting() {
        App app = new App();
        assertThat(app.getGreeting()).isEqualTo("Hello World!");
    }
}