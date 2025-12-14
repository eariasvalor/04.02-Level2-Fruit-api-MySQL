package cat.itacademy.s04.t02.n02.fruit.controller;

import cat.itacademy.s04.t02.n02.fruit.dto.ProviderRequestDTO;
import cat.itacademy.s04.t02.n02.fruit.dto.ProviderResponseDTO;
import cat.itacademy.s04.t02.n02.fruit.exception.ResourceConflictException;
import cat.itacademy.s04.t02.n02.fruit.service.ProviderService;
import cat.itacademy.s04.t02.n02.fruit.exception.ResourceNotFoundException;
import cat.itacademy.s04.t02.n02.fruit.exception.DuplicateResourceException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ProviderController.class)
class ProviderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ProviderService providerService;

    @Test
    void createProvider_WithValidData_Returns201Created() throws Exception {
        ProviderRequestDTO request = new ProviderRequestDTO("Fruits Inc", "Spain");
        ProviderResponseDTO response = new ProviderResponseDTO(1L, "Fruits Inc", "Spain");

        when(providerService.createProvider(any(ProviderRequestDTO.class)))
                .thenReturn(response);

        mockMvc.perform(post("/providers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Fruits Inc"))
                .andExpect(jsonPath("$.country").value("Spain"));
    }

    @Test
    void createProvider_WithBlankName_Returns400BadRequest() throws Exception {
        ProviderRequestDTO request = new ProviderRequestDTO("", "Spain");

        mockMvc.perform(post("/providers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createProvider_WithBlankCountry_Returns400BadRequest() throws Exception {
        ProviderRequestDTO request = new ProviderRequestDTO("Fruits Inc", "");

        mockMvc.perform(post("/providers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getAllProviders_ReturnsEmptyList() throws Exception {
        when(providerService.getAllProviders()).thenReturn(List.of());

        mockMvc.perform(get("/providers"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$").isEmpty());
    }

    @Test
    void getAllProviders_ReturnsListOfProviders() throws Exception {
        List<ProviderResponseDTO> providers = List.of(
                new ProviderResponseDTO(1L, "Fruits Inc", "Spain"),
                new ProviderResponseDTO(2L, "Veggies Ltd", "France")
        );

        when(providerService.getAllProviders()).thenReturn(providers);

        mockMvc.perform(get("/providers"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].name").value("Fruits Inc"))
                .andExpect(jsonPath("$[0].country").value("Spain"))
                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[1].name").value("Veggies Ltd"))
                .andExpect(jsonPath("$[1].country").value("France"));
    }

    @Test
    void updateProvider_WithValidData_Returns200Ok() throws Exception {
        Long providerId = 1L;
        ProviderRequestDTO request = new ProviderRequestDTO("Updated Fruits Inc", "Italy");
        ProviderResponseDTO response = new ProviderResponseDTO(providerId, "Updated Fruits Inc", "Italy");

        when(providerService.updateProvider(eq(providerId), any(ProviderRequestDTO.class)))
                .thenReturn(response);

        mockMvc.perform(put("/providers/{id}", providerId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(providerId))
                .andExpect(jsonPath("$.name").value("Updated Fruits Inc"))
                .andExpect(jsonPath("$.country").value("Italy"));
    }

    @Test
    void updateProvider_WithNonExistentId_Returns404NotFound() throws Exception {
        Long providerId = 999L;
        ProviderRequestDTO request = new ProviderRequestDTO("Fruits Inc", "Spain");

        when(providerService.updateProvider(eq(providerId), any(ProviderRequestDTO.class)))
                .thenThrow(new ResourceNotFoundException("Provider with id " + providerId + " not found"));

        mockMvc.perform(put("/providers/{id}", providerId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Provider with id 999 not found"));
    }

    @Test
    void updateProvider_WithBlankName_Returns400BadRequest() throws Exception {
        Long providerId = 1L;
        ProviderRequestDTO request = new ProviderRequestDTO("", "Spain");

        mockMvc.perform(put("/providers/{id}", providerId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void updateProvider_WithDuplicateName_Returns409Conflict() throws Exception {
        Long providerId = 1L;
        ProviderRequestDTO request = new ProviderRequestDTO("Existing Provider", "Spain");

        when(providerService.updateProvider(eq(providerId), any(ProviderRequestDTO.class)))
                .thenThrow(new DuplicateResourceException("Provider with name 'Existing Provider' already exists"));

        mockMvc.perform(put("/providers/{id}", providerId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value("Provider with name 'Existing Provider' already exists"));
    }

    @Test
    void deleteProvider_WithValidId_Returns204NoContent() throws Exception {
        Long providerId = 1L;

        doNothing().when(providerService).deleteProvider(providerId);

        mockMvc.perform(delete("/providers/{id}", providerId))
                .andExpect(status().isNoContent());
    }

    @Test
    void deleteProvider_WithNonExistentId_Returns404NotFound() throws Exception {
        Long providerId = 999L;

        doThrow(new ResourceNotFoundException("Provider with id " + providerId + " not found"))
                .when(providerService).deleteProvider(providerId);

        mockMvc.perform(delete("/providers/{id}", providerId))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Provider with id 999 not found"));
    }

    @Test
    void deleteProvider_WithAssociatedFruits_Returns409Conflict() throws Exception {
        Long providerId = 1L;

        doThrow(new ResourceConflictException("Cannot delete provider with id " + providerId + " because it has associated fruits"))
                .when(providerService).deleteProvider(providerId);

        mockMvc.perform(delete("/providers/{id}", providerId))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value("Cannot delete provider with id 1 because it has associated fruits"));
    }

}
