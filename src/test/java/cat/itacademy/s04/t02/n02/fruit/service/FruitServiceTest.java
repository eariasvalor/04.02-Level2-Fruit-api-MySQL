package cat.itacademy.s04.t02.n02.fruit.service;

import cat.itacademy.s04.t02.n02.fruit.dto.FruitRequestDTO;
import cat.itacademy.s04.t02.n02.fruit.dto.FruitResponseDTO;
import cat.itacademy.s04.t02.n02.fruit.dto.ProviderResponseDTO;
import cat.itacademy.s04.t02.n02.fruit.exception.ResourceNotFoundException;
import cat.itacademy.s04.t02.n02.fruit.mapper.FruitMapper;
import cat.itacademy.s04.t02.n02.fruit.model.Fruit;
import cat.itacademy.s04.t02.n02.fruit.model.Provider;
import cat.itacademy.s04.t02.n02.fruit.repository.FruitRepository;
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
class FruitServiceTest {

    @Mock
    private FruitRepository fruitRepository;

    @Mock
    private ProviderRepository providerRepository;

    @Mock
    private FruitMapper fruitMapper;

    @InjectMocks
    private FruitServiceImpl fruitService;

    @Test
    void createFruit_WithValidProvider_ReturnsFruitResponse() {
        FruitRequestDTO request = new FruitRequestDTO("Apple", 10, 1L);

        Provider provider = new Provider(1L, "Fruits Inc", "Spain");
        Fruit fruit = new Fruit(null, "Apple", 10, provider);
        Fruit savedFruit = new Fruit(1L, "Apple", 10, provider);

        ProviderResponseDTO providerResponse = new ProviderResponseDTO(1L, "Fruits Inc", "Spain");
        FruitResponseDTO expectedResponse = new FruitResponseDTO(1L, "Apple", 10, providerResponse);

        when(providerRepository.findById(1L)).thenReturn(Optional.of(provider));
        when(fruitMapper.toEntity(request, provider)).thenReturn(fruit);
        when(fruitRepository.save(fruit)).thenReturn(savedFruit);
        when(fruitMapper.toResponseDTO(savedFruit)).thenReturn(expectedResponse);

        FruitResponseDTO result = fruitService.createFruit(request);

        assertThat(result).isNotNull();
        assertThat(result.id()).isEqualTo(1L);
        assertThat(result.name()).isEqualTo("Apple");
        assertThat(result.weightInKilos()).isEqualTo(10);
        assertThat(result.provider().id()).isEqualTo(1L);
    }

    @Test
    void createFruit_WithNonExistentProvider_ThrowsResourceNotFoundException() {
        FruitRequestDTO request = new FruitRequestDTO("Apple", 10, 999L);

        when(providerRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> fruitService.createFruit(request))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Provider with id 999 not found");
    }

    @Test
    void getFruitsByProviderId_WithExistingProvider_ReturnsListOfFruits() {
        Long providerId = 1L;
        Provider provider = new Provider(providerId, "Fruits Inc", "Spain");

        Fruit fruit1 = new Fruit(1L, "Apple", 10, provider);
        Fruit fruit2 = new Fruit(2L, "Banana", 5, provider);
        List<Fruit> fruits = List.of(fruit1, fruit2);

        ProviderResponseDTO providerResponse = new ProviderResponseDTO(providerId, "Fruits Inc", "Spain");
        FruitResponseDTO fruitResponse1 = new FruitResponseDTO(1L, "Apple", 10, providerResponse);
        FruitResponseDTO fruitResponse2 = new FruitResponseDTO(2L, "Banana", 5, providerResponse);

        when(providerRepository.existsById(providerId)).thenReturn(true);
        when(fruitRepository.findByProviderId(providerId)).thenReturn(fruits);
        when(fruitMapper.toResponseDTO(fruit1)).thenReturn(fruitResponse1);
        when(fruitMapper.toResponseDTO(fruit2)).thenReturn(fruitResponse2);

        List<FruitResponseDTO> result = fruitService.getFruitsByProviderId(providerId);

        assertThat(result).hasSize(2);
        assertThat(result.get(0).name()).isEqualTo("Apple");
        assertThat(result.get(1).name()).isEqualTo("Banana");
    }

    @Test
    void getFruitsByProviderId_WithNonExistentProvider_ThrowsResourceNotFoundException() {
        Long providerId = 999L;

        when(providerRepository.existsById(providerId)).thenReturn(false);

        assertThatThrownBy(() -> fruitService.getFruitsByProviderId(providerId))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Provider with id 999 not found");
    }

    @Test
    void getFruitsByProviderId_WithExistingProviderButNoFruits_ReturnsEmptyList() {
        Long providerId = 1L;

        when(providerRepository.existsById(providerId)).thenReturn(true);
        when(fruitRepository.findByProviderId(providerId)).thenReturn(List.of());

        List<FruitResponseDTO> result = fruitService.getFruitsByProviderId(providerId);

        assertThat(result).isEmpty();
    }
}
