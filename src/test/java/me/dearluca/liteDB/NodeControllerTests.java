package me.dearluca.liteDB;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class NodeControllerTests {
    @Autowired
    private MockMvc mockMvc;

    @Test
    void testNodeGetInfo() throws Exception {
        mockMvc.perform(get("/node/info"))
                .andExpect(status().isOk());
    }

    @Test
    void testGetHashRing() throws Exception {
        mockMvc.perform(get("/node/ring")).andExpect(status().isOk());
    }

    @Test
    void testGetPrimaryNode() throws Exception {
        mockMvc.perform(get("/node/primary/user:1")).andExpect(status().isOk());
    }

    @Test
    void testGetReplicas() throws Exception {
        mockMvc.perform(get("/node/replicas/user:1/2")).andExpect(status().isOk());
    }

}
