package cat.itacademy.s04.t02.n02.fruit.service;

import cat.itacademy.s04.t02.n02.fruit.dto.ProviderRequestDTO;
import cat.itacademy.s04.t02.n02.fruit.dto.ProviderResponseDTO;
import cat.itacademy.s04.t02.n02.fruit.exception.DuplicateResourceException;
import cat.itacademy.s04.t02.n02.fruit.exception.ResourceNotFoundException;
import cat.itacademy.s04.t02.n02.fruit.mapper.ProviderMapper;
import cat.itacademy.s04.t02.n02.fruit.model.Provider;
import cat.itacademy.s04.t02.n02.fruit.repository.ProviderRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProviderServiceTest {

    @Mock
    private ProviderRepository providerRepository;

    @Mock
    private ProviderMapper providerMapper;

    @InjectMocks
    private ProviderServiceImpl providerService;

    @Test
    void createProvider_WithValidData_ReturnsProviderResponse() {
        ProviderRequestDTO request = new ProviderRequestDTO("Fruits Inc", "Spain");
        Provider entity = new Provider(null, "Fruits Inc", "Spain");
        Provider savedEntity = new Provider(1L, "Fruits Inc", "Spain");
        ProviderResponseDTO expectedResponse = new ProviderResponseDTO(1L, "Fruits Inc", "Spain");

        when(providerRepository.existsByName("Fruits Inc")).thenReturn(false);
        when(providerMapper.toEntity(request)).thenReturn(entity);
        when(providerRepository.save(entity)).thenReturn(savedEntity);
        when(providerMapper.toResponseDTO(savedEntity)).thenReturn(expectedResponse);

        ProviderResponseDTO result = providerService.createProvider(request);

        assertThat(result).isNotNull();
        assertThat(result.id()).isEqualTo(1L);
        assertThat(result.name()).isEqualTo("Fruits Inc");
        assertThat(result.country()).isEqualTo("Spain");
    }

    @Test
    void createProvider_WithDuplicateName_ThrowsDuplicateResourceException() {
        ProviderRequestDTO request = new ProviderRequestDTO("Fruits Inc", "Spain");

        when(providerRepository.existsByName("Fruits Inc")).thenReturn(true);

        assertThatThrownBy(() -> providerService.createProvider(request))
                .isInstanceOf(DuplicateResourceException.class)
                .hasMessage("Provider with name 'Fruits Inc' already exists");
    }

    @Test
    void getAllProviders_WhenNoProviders_ReturnsEmptyList() {
        when(providerRepository.findAll()).thenReturn(List.of());

        List<ProviderResponseDTO> result = providerService.getAllProviders();

        assertThat(result).isEmpty();
    }

    @Test
    void getAllProviders_WhenProvidersExist_ReturnsListOfProviders() {
        Provider provider1 = new Provider(1L, "Fruits Inc", "Spain");
        Provider provider2 = new Provider(2L, "Veggies Ltd", "France");
        List<Provider> providers = List.of(provider1, provider2);

        ProviderResponseDTO response1 = new ProviderResponseDTO(1L, "Fruits Inc", "Spain");
        ProviderResponseDTO response2 = new ProviderResponseDTO(2L, "Veggies Ltd", "France");

        when(providerRepository.findAll()).thenReturn(providers);
        when(providerMapper.toResponseDTO(provider1)).thenReturn(response1);
        when(providerMapper.toResponseDTO(provider2)).thenReturn(response2);

        List<ProviderResponseDTO> result = providerService.getAllProviders();

        assertThat(result).hasSize(2);
        assertThat(result.get(0).name()).isEqualTo("Fruits Inc");
        assertThat(result.get(1).name()).isEqualTo("Veggies Ltd");
    }

    @Test
    void updateProvider_WithValidData_ReturnsUpdatedProvider() {
        Long providerId = 1L;
        ProviderRequestDTO request = new ProviderRequestDTO("Updated Fruits Inc", "Italy");

        Provider existingProvider = new Provider(providerId, "Fruits Inc", "Spain");
        Provider updatedProvider = new Provider(providerId, "Updated Fruits Inc", "Italy");
        ProviderResponseDTO expectedResponse = new ProviderResponseDTO(providerId, "Updated Fruits Inc", "Italy");

        when(providerRepository.findById(providerId)).thenReturn(Optional.of(existingProvider));
        when(providerRepository.existsByName("Updated Fruits Inc")).thenReturn(false);
        when(providerRepository.save(any(Provider.class))).thenReturn(updatedProvider);
        when(providerMapper.toResponseDTO(updatedProvider)).thenReturn(expectedResponse);

        ProviderResponseDTO result = providerService.updateProvider(providerId, request);

        assertThat(result).isNotNull();
        assertThat(result.id()).isEqualTo(providerId);
        assertThat(result.name()).isEqualTo("Updated Fruits Inc");
        assertThat(result.country()).isEqualTo("Italy");
    }

    @Test
    void updateProvider_WithNonExistentId_ThrowsResourceNotFoundException() {
        Long providerId = 999L;
        ProviderRequestDTO request = new ProviderRequestDTO("Fruits Inc", "Spain");

        when(providerRepository.findById(providerId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> providerService.updateProvider(providerId, request))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Provider with id 999 not found");
    }

    @Test
    void updateProvider_WithDuplicateName_ThrowsDuplicateResourceException() {
        Long providerId = 1L;
        ProviderRequestDTO request = new ProviderRequestDTO("Existing Provider", "Spain");

        Provider existingProvider = new Provider(providerId, "Fruits Inc", "Spain");

        when(providerRepository.findById(providerId)).thenReturn(Optional.of(existingProvider));
        when(providerRepository.existsByName("Existing Provider")).thenReturn(true);

        assertThatThrownBy(() -> providerService.updateProvider(providerId, request))
                .isInstanceOf(DuplicateResourceException.class)
                .hasMessage("Provider with name 'Existing Provider' already exists");
    }

    @Test
    void updateProvider_WithSameName_UpdatesSuccessfully() {
        Long providerId = 1L;
        ProviderRequestDTO request = new ProviderRequestDTO("Fruits Inc", "Italy");

        Provider existingProvider = new Provider(providerId, "Fruits Inc", "Spain");
        Provider updatedProvider = new Provider(providerId, "Fruits Inc", "Italy");
        ProviderResponseDTO expectedResponse = new ProviderResponseDTO(providerId, "Fruits Inc", "Italy");

        when(providerRepository.findById(providerId)).thenReturn(Optional.of(existingProvider));
        when(providerRepository.save(any(Provider.class))).thenReturn(updatedProvider);
        when(providerMapper.toResponseDTO(updatedProvider)).thenReturn(expectedResponse);

        ProviderResponseDTO result = providerService.updateProvider(providerId, request);

        assertThat(result).isNotNull();
        assertThat(result.country()).isEqualTo("Italy");
    }
}
