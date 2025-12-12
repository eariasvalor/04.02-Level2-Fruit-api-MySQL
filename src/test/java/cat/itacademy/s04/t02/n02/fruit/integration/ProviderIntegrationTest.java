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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
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
    @Test
    void updateProvider_WithValidData_ReturnsUpdatedProvider() throws Exception {
                ProviderRequestDTO createRequest = new ProviderRequestDTO("Fruits Inc", "Spain");
        String createResponse = mockMvc.perform(post("/providers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createRequest)))
                .andReturn().getResponse().getContentAsString();

        Long providerId = objectMapper.readTree(createResponse).get("id").asLong();

                ProviderRequestDTO updateRequest = new ProviderRequestDTO("Updated Fruits Inc", "Italy");

        mockMvc.perform(put("/providers/{id}", providerId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(providerId))
                .andExpect(jsonPath("$.name").value("Updated Fruits Inc"))
                .andExpect(jsonPath("$.country").value("Italy"));
    }

    @Test
    void updateProvider_WithNonExistentId_Returns404NotFound() throws Exception {
                Long nonExistentId = 999L;
        ProviderRequestDTO request = new ProviderRequestDTO("Fruits Inc", "Spain");

                mockMvc.perform(put("/providers/{id}", nonExistentId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Provider with id 999 not found"));
    }

    @Test
    void updateProvider_WithDuplicateName_Returns409Conflict() throws Exception {
                ProviderRequestDTO request1 = new ProviderRequestDTO("Fruits Inc", "Spain");
        ProviderRequestDTO request2 = new ProviderRequestDTO("Veggies Ltd", "France");

        String response1 = mockMvc.perform(post("/providers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request1)))
                .andReturn().getResponse().getContentAsString();

        mockMvc.perform(post("/providers")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request2)));

        Long provider1Id = objectMapper.readTree(response1).get("id").asLong();

                ProviderRequestDTO updateRequest = new ProviderRequestDTO("Veggies Ltd", "Italy");

                mockMvc.perform(put("/providers/{id}", provider1Id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value("Provider with name 'Veggies Ltd' already exists"));
    }

    @Test
    void updateProvider_WithBlankName_Returns400BadRequest() throws Exception {
                ProviderRequestDTO createRequest = new ProviderRequestDTO("Fruits Inc", "Spain");
        String createResponse = mockMvc.perform(post("/providers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createRequest)))
                .andReturn().getResponse().getContentAsString();

        Long providerId = objectMapper.readTree(createResponse).get("id").asLong();

                ProviderRequestDTO updateRequest = new ProviderRequestDTO("", "Italy");

                mockMvc.perform(put("/providers/{id}", providerId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isBadRequest());
    }
}
