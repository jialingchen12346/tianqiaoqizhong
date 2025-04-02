package com.workshop;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class WorkshopApplicationTests {

    @Test
    void contextLoads() {
        // Verify Spring context loads successfully
        assertTrue(true, "Context should load without errors");
    }

}
