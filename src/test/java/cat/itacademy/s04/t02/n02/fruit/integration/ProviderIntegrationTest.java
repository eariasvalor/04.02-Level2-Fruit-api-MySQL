package cat.itacademy.s04.t02.n02.fruit.integration;

import cat.itacademy.s04.t02.n02.fruit.dto.ProviderRequestDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class ProviderIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void createProvider_EndToEnd_ReturnsCreatedProvider() throws Exception {
        ProviderRequestDTO request = new ProviderRequestDTO("Fruits Inc", "Spain");

        mockMvc.perform(post("/providers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.name").value("Fruits Inc"))
                .andExpect(jsonPath("$.country").value("Spain"));
    }

    @Test
    void createProvider_WithDuplicateName_Returns409Conflict() throws Exception {
        ProviderRequestDTO request1 = new ProviderRequestDTO("Fruits Inc", "Spain");
        mockMvc.perform(post("/providers")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request1)));

        ProviderRequestDTO request2 = new ProviderRequestDTO("Fruits Inc", "France");

        mockMvc.perform(post("/providers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request2)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value("Provider with name 'Fruits Inc' already exists"));
    }

    @Test
    void createProvider_WithBlankName_Returns400BadRequest() throws Exception {
        ProviderRequestDTO request = new ProviderRequestDTO("", "Spain");

        mockMvc.perform(post("/providers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").exists());
    }

    @Test
    void createProvider_WithBlankCountry_Returns400BadRequest() throws Exception {
        ProviderRequestDTO request = new ProviderRequestDTO("Fruits Inc", "");

        mockMvc.perform(post("/providers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").exists());
    }

    @Test
    void getAllProviders_WhenEmpty_ReturnsEmptyArray() throws Exception {
        mockMvc.perform(get("/providers"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$").isEmpty());
    }

    @Test
    void getAllProviders_WhenProvidersExist_ReturnsAllProviders() throws Exception {
        ProviderRequestDTO request1 = new ProviderRequestDTO("Fruits Inc", "Spain");
        ProviderRequestDTO request2 = new ProviderRequestDTO("Veggies Ltd", "France");

        mockMvc.perform(post("/providers")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request1)));

        mockMvc.perform(post("/providers")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request2)));

        mockMvc.perform(get("/providers"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].name").value("Fruits Inc"))
                .andExpect(jsonPath("$[1].name").value("Veggies Ltd"));
    }
}
