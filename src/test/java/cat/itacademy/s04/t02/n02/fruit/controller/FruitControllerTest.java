package cat.itacademy.s04.t02.n02.fruit.controller;

import cat.itacademy.s04.t02.n02.fruit.dto.FruitRequestDTO;
import cat.itacademy.s04.t02.n02.fruit.dto.FruitResponseDTO;
import cat.itacademy.s04.t02.n02.fruit.dto.ProviderResponseDTO;
import cat.itacademy.s04.t02.n02.fruit.exception.ResourceNotFoundException;
import cat.itacademy.s04.t02.n02.fruit.service.FruitService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(FruitController.class)
class FruitControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private FruitService fruitService;

    @Test
    void createFruit_WithValidData_Returns201Created() throws Exception {
                FruitRequestDTO request = new FruitRequestDTO("Apple", 10, 1L);
        ProviderResponseDTO provider = new ProviderResponseDTO(1L, "Fruits Inc", "Spain");
        FruitResponseDTO response = new FruitResponseDTO(1L, "Apple", 10, provider);

        when(fruitService.createFruit(any(FruitRequestDTO.class)))
                .thenReturn(response);

                mockMvc.perform(post("/fruits")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Apple"))
                .andExpect(jsonPath("$.weightInKilos").value(10))
                .andExpect(jsonPath("$.provider.id").value(1))
                .andExpect(jsonPath("$.provider.name").value("Fruits Inc"));
    }

    @Test
    void createFruit_WithNonExistentProvider_Returns404NotFound() throws Exception {
                FruitRequestDTO request = new FruitRequestDTO("Apple", 10, 999L);

        when(fruitService.createFruit(any(FruitRequestDTO.class)))
                .thenThrow(new ResourceNotFoundException("Provider with id 999 not found"));

                mockMvc.perform(post("/fruits")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Provider with id 999 not found"));
    }

    @Test
    void createFruit_WithBlankName_Returns400BadRequest() throws Exception {
                FruitRequestDTO request = new FruitRequestDTO("", 10, 1L);

                mockMvc.perform(post("/fruits")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createFruit_WithNegativeWeight_Returns400BadRequest() throws Exception {
                FruitRequestDTO request = new FruitRequestDTO("Apple", -5, 1L);

                mockMvc.perform(post("/fruits")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
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
