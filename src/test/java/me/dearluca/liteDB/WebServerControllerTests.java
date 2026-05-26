package me.dearluca.liteDB;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.client.match.MockRestRequestMatchers.content;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class WebServerControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void testGetNotFound() throws Exception {
        mockMvc.perform(get("/kv/user:1")).andExpect(status().isNotFound());
    }

    @Test
    void testPut() throws Exception {
        mockMvc.perform(put("/kv/user:2").content("Mario"))
                .andExpect(status().isOk());
    }

    @Test
    void testPutThenGet() throws Exception {
        mockMvc.perform(put("/kv/user:3")
                        .content("John"))
                .andExpect(status().isOk());

        mockMvc.perform(get("/kv/user:3"))
                .andExpect(status().isOk())
                .andExpect(result -> content().string("John"));
    }
}
