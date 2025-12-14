package cat.itacademy.s04.t02.n02.fruit.controller;

import cat.itacademy.s04.t02.n02.fruit.dto.FruitRequestDTO;
import cat.itacademy.s04.t02.n02.fruit.dto.FruitResponseDTO;
import cat.itacademy.s04.t02.n02.fruit.dto.ProviderResponseDTO;
import cat.itacademy.s04.t02.n02.fruit.exception.ResourceNotFoundException;
import cat.itacademy.s04.t02.n02.fruit.repository.FruitRepository;
import cat.itacademy.s04.t02.n02.fruit.service.FruitService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
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

    @Test
    void getFruitsByProviderId_WithExistingProvider_ReturnsListOfFruits() throws Exception {
        Long providerId = 1L;
        ProviderResponseDTO provider = new ProviderResponseDTO(1L, "Fruits Inc", "Spain");
        List<FruitResponseDTO> fruits = List.of(
                new FruitResponseDTO(1L, "Apple", 10, provider),
                new FruitResponseDTO(2L, "Banana", 5, provider)
        );

        when(fruitService.getFruitsByProviderId(providerId)).thenReturn(fruits);

        mockMvc.perform(get("/fruits")
                        .param("providerId", providerId.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].name").value("Apple"))
                .andExpect(jsonPath("$[1].name").value("Banana"))
                .andExpect(jsonPath("$[0].provider.id").value(providerId));
    }

    @Test
    void getFruitsByProviderId_WithEmptyResult_ReturnsEmptyArray() throws Exception {
        Long providerId = 1L;

        when(fruitService.getFruitsByProviderId(providerId)).thenReturn(List.of());

        mockMvc.perform(get("/fruits")
                        .param("providerId", providerId.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$").isEmpty());
    }

    @Test
    void getFruitsByProviderId_WithNonExistentProvider_Returns404NotFound() throws Exception {
        Long providerId = 999L;

        when(fruitService.getFruitsByProviderId(providerId))
                .thenThrow(new ResourceNotFoundException("Provider with id 999 not found"));

        mockMvc.perform(get("/fruits")
                        .param("providerId", providerId.toString()))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Provider with id 999 not found"));
    }

    @Test
    void getAllFruits_ReturnsEmptyList() throws Exception {
                when(fruitService.getAllFruits()).thenReturn(List.of());

                mockMvc.perform(get("/fruits/all"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$").isEmpty());
    }

    @Test
    void getAllFruits_ReturnsListOfFruits() throws Exception {
                ProviderResponseDTO provider = new ProviderResponseDTO(1L, "Fruits Inc", "Spain");
        List<FruitResponseDTO> fruits = List.of(
                new FruitResponseDTO(1L, "Apple", 10, provider),
                new FruitResponseDTO(2L, "Banana", 5, provider)
        );

        when(fruitService.getAllFruits()).thenReturn(fruits);

                mockMvc.perform(get("/fruits/all"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].name").value("Apple"))
                .andExpect(jsonPath("$[0].weightInKilos").value(10))
                .andExpect(jsonPath("$[0].provider.name").value("Fruits Inc"))
                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[1].name").value("Banana"));
    }

    @Test
    void getFruitById_WithExistingId_Returns200Ok() throws Exception {
                Long fruitId = 1L;
        ProviderResponseDTO provider = new ProviderResponseDTO(1L, "Fruits Inc", "Spain");
        FruitResponseDTO response = new FruitResponseDTO(fruitId, "Apple", 10, provider);

        when(fruitService.getFruitById(fruitId)).thenReturn(response);

                mockMvc.perform(get("/fruits/{id}", fruitId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(fruitId))
                .andExpect(jsonPath("$.name").value("Apple"))
                .andExpect(jsonPath("$.weightInKilos").value(10))
                .andExpect(jsonPath("$.provider.id").value(1))
                .andExpect(jsonPath("$.provider.name").value("Fruits Inc"));
    }

    @Test
    void getFruitById_WithNonExistentId_Returns404NotFound() throws Exception {
                Long fruitId = 999L;

        when(fruitService.getFruitById(fruitId))
                .thenThrow(new ResourceNotFoundException("Fruit with id 999 not found"));

                mockMvc.perform(get("/fruits/{id}", fruitId))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Fruit with id 999 not found"));
    }

    @Test
    void updateFruit_WithValidData_Returns2000k() throws Exception {
        Long fruitId = 1L;
        FruitRequestDTO request = new FruitRequestDTO("Updated Apple", 15, 2L);
        ProviderResponseDTO provider = new ProviderResponseDTO(2L, "Veggies Ltd", "France");
        FruitResponseDTO response = new FruitResponseDTO(fruitId, "Updated Apple", 15, provider);

        when(fruitService.updateFruit(eq(fruitId), any(FruitRequestDTO.class)))
                .thenReturn(response);

        mockMvc.perform(put("/fruits/{id}", fruitId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(fruitId))
                .andExpect(jsonPath("$.name").value("Updated Apple"))
                .andExpect(jsonPath("$.weightInKilos").value(15))
                .andExpect(jsonPath("$.provider.id").value(2));
    }

    @Test
    void updateFruit_WithNonExistingId_Returns404NotFound() throws Exception {
        Long fruitId = 999L;
        FruitRequestDTO request = new FruitRequestDTO("Apple", 10, 1L);

        when(fruitService.updateFruit(eq(fruitId), any(FruitRequestDTO.class)))
                .thenThrow(new ResourceNotFoundException("Fruit with id 999 not found"));

        mockMvc.perform(put("/fruits/{id}", fruitId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Fruit with id 999 not found"));
    }

    @Test
    void updateFruit_WithBlankName_Returns400BadRequest() throws Exception {
        Long fruitId = 1L;
        FruitRequestDTO request = new FruitRequestDTO("", 10, 1L);

        mockMvc.perform(put("/fruits/{id}", fruitId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void updateFruit_WithNegativeWeight_Returns400BadRequest() throws Exception {
        Long fruitId = 1L;
        FruitRequestDTO request = new FruitRequestDTO("Apple", -5, 1L);

        mockMvc.perform(put("/fruits/{id}", fruitId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

}
