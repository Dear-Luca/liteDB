package me.dearluca.liteDB;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;

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
}
