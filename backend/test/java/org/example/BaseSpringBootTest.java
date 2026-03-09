package org.example;

import org.example.config.TestConfig;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@SpringBootTest
@Import(TestConfig.class)
@ExtendWith(SpringExtension.class)
public class BaseSpringBootTest {
}