package vn.edu.bookstore.legacy.adapter.api;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import vn.edu.bookstore.legacy.adapter.LegacySoapTestServer;

@SpringBootTest
@AutoConfigureMockMvc
class LegacyCatalogTimeoutTest {

    static final LegacySoapTestServer SOAP = LegacySoapTestServer.start(5000);

    @DynamicPropertySource
    static void props(DynamicPropertyRegistry r) {
        r.add("bookstore.legacy.endpoint", SOAP::url);
    }

    @AfterAll
    static void stop() {
        SOAP.close();
    }

    @Autowired
    MockMvc mvc;

    @Test
    void heThongKeThuaChamTraVe504() throws Exception {
        mvc.perform(get("/api/v1/legacy/books/9780134685991"))
                .andExpect(status().isGatewayTimeout())
                .andExpect(content().contentTypeCompatibleWith(
                        MediaType.valueOf("application/problem+json")))
                .andExpect(jsonPath("$.status").value(504));
    }
}
