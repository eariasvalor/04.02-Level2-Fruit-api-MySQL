package cat.itacademy.s04.t02.n02.fruit.integration;

import cat.itacademy.s04.t02.n02.fruit.dto.FruitRequestDTO;
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
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class FruitIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void createFruit_WithValidProvider_ReturnsCreatedFruit() throws Exception {
                ProviderRequestDTO providerRequest = new ProviderRequestDTO("Fruits Inc", "Spain");
        String providerResponse = mockMvc.perform(post("/providers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(providerRequest)))
                .andReturn().getResponse().getContentAsString();

        Long providerId = objectMapper.readTree(providerResponse).get("id").asLong();

                FruitRequestDTO fruitRequest = new FruitRequestDTO("Apple", 10, providerId);

                mockMvc.perform(post("/fruits")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(fruitRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.name").value("Apple"))
                .andExpect(jsonPath("$.weightInKilos").value(10))
                .andExpect(jsonPath("$.provider.id").value(providerId))
                .andExpect(jsonPath("$.provider.name").value("Fruits Inc"))
                .andExpect(jsonPath("$.provider.country").value("Spain"));
    }

    @Test
    void createFruit_WithNonExistentProvider_Returns404NotFound() throws Exception {
                Long nonExistentProviderId = 999L;
        FruitRequestDTO request = new FruitRequestDTO("Apple", 10, nonExistentProviderId);

                mockMvc.perform(post("/fruits")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Provider with id 999 not found"));
    }

    @Test
    void createFruit_WithBlankName_Returns400BadRequest() throws Exception {
                ProviderRequestDTO providerRequest = new ProviderRequestDTO("Fruits Inc", "Spain");
        String providerResponse = mockMvc.perform(post("/providers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(providerRequest)))
                .andReturn().getResponse().getContentAsString();

        Long providerId = objectMapper.readTree(providerResponse).get("id").asLong();

                FruitRequestDTO fruitRequest = new FruitRequestDTO("", 10, providerId);

                mockMvc.perform(post("/fruits")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(fruitRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createFruit_WithNegativeWeight_Returns400BadRequest() throws Exception {
                ProviderRequestDTO providerRequest = new ProviderRequestDTO("Fruits Inc", "Spain");
        String providerResponse = mockMvc.perform(post("/providers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(providerRequest)))
                .andReturn().getResponse().getContentAsString();

        Long providerId = objectMapper.readTree(providerResponse).get("id").asLong();

                FruitRequestDTO fruitRequest = new FruitRequestDTO("Apple", -5, providerId);

                mockMvc.perform(post("/fruits")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(fruitRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createFruit_WithNullProviderId_Returns400BadRequest() throws Exception {
                FruitRequestDTO request = new FruitRequestDTO("Apple", 10, null);

                mockMvc.perform(post("/fruits")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }
}
