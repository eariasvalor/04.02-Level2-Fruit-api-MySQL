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

    @Test
    void getAllFruits_WhenNoFruits_ReturnsEmptyList() {
        when(fruitRepository.findAll()).thenReturn(List.of());

        List<FruitResponseDTO> result = fruitService.getAllFruits();

        assertThat(result).isEmpty();
    }

    @Test
    void getAllFruits_WhenFruitsExist_ReturnsListOfFruits() {
        Provider provider = new Provider(1L, "Fruits Inc", "Spain");
        Fruit fruit1 = new Fruit(1L, "Apple", 10, provider);
        Fruit fruit2 = new Fruit(2L, "Banana", 5, provider);
        List<Fruit> fruits = List.of(fruit1, fruit2);

        ProviderResponseDTO providerResponse = new ProviderResponseDTO(1L, "Fruits Inc", "Spain");
        FruitResponseDTO fruitResponse1 = new FruitResponseDTO(1L, "Apple", 10, providerResponse);
        FruitResponseDTO fruitResponse2 = new FruitResponseDTO(2L, "Banana", 5, providerResponse);

        when(fruitRepository.findAll()).thenReturn(fruits);
        when(fruitMapper.toResponseDTO(fruit1)).thenReturn(fruitResponse1);
        when(fruitMapper.toResponseDTO(fruit2)).thenReturn(fruitResponse2);

        List<FruitResponseDTO> result = fruitService.getAllFruits();

        assertThat(result).hasSize(2);
        assertThat(result.get(0).name()).isEqualTo("Apple");
        assertThat(result.get(1).name()).isEqualTo("Banana");
    }

    @Test
    void getFruitById_WithExistingId_ReturnsFruit() {
        Long fruitId = 1L;
        Provider provider = new Provider(1L, "Fruits Inc", "Spain");
        Fruit fruit = new Fruit(fruitId, "Apple", 10, provider);

        ProviderResponseDTO providerResponse = new ProviderResponseDTO(1L, "Fruits Inc", "Spain");
        FruitResponseDTO expectedResponse = new FruitResponseDTO(fruitId, "Apple", 10, providerResponse);

        when(fruitRepository.findById(fruitId)).thenReturn(Optional.of(fruit));
        when(fruitMapper.toResponseDTO(fruit)).thenReturn(expectedResponse);

        FruitResponseDTO result = fruitService.getFruitById(fruitId);

        assertThat(result).isNotNull();
        assertThat(result.id()).isEqualTo(fruitId);
        assertThat(result.name()).isEqualTo("Apple");
        assertThat(result.weightInKilos()).isEqualTo(10);
        assertThat(result.provider().name()).isEqualTo("Fruits Inc");
    }

    @Test
    void getFruitById_WithNonExistentId_ThrowsResourceNotFoundException() {
        Long fruitId = 999L;

        when(fruitRepository.findById(fruitId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> fruitService.getFruitById(fruitId))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Fruit with id 999 not found");
    }

    @Test
    void updateFruit_WithValidData_ReturnsUpdatedFruit() {
        Long fruitId = 1L;
        FruitRequestDTO request = new FruitRequestDTO("Updated Apple", 15, 2L);

        Provider oldProvider = new Provider(1L, "Fruits Inc", "Spain");
        Provider newProvider = new Provider(2L, "Veggies Ltd", "France");

        Fruit existingFruit = new Fruit(fruitId, "Apple", 10, oldProvider);
        Fruit updatedFruit = new Fruit(fruitId, "Updated Apple", 15, newProvider);

        ProviderResponseDTO providerResponse = new ProviderResponseDTO(2L, "Veggies Ltd", "France");
        FruitResponseDTO expectedResponse = new FruitResponseDTO(fruitId, "Updated Apple", 15, providerResponse);

        when(fruitRepository.findById(fruitId)).thenReturn(Optional.of(existingFruit));
        when(providerRepository.findById(2L)).thenReturn(Optional.of(newProvider));
        when(fruitRepository.save(any(Fruit.class))).thenReturn(updatedFruit);
        when(fruitMapper.toResponseDTO(updatedFruit)).thenReturn(expectedResponse);

        FruitResponseDTO result = fruitService.updateFruit(fruitId, request);

        assertThat(result).isNotNull();
        assertThat(result.id()).isEqualTo(fruitId);
        assertThat(result.name()).isEqualTo("Updated Apple");
        assertThat(result.weightInKilos()).isEqualTo(15);
        assertThat(result.provider().id()).isEqualTo(2L);
    }

    @Test
    void updateFruit_WithNonExistentFruitId_ThrowsResourceNotFoundException() {
        Long fruitId = 999L;
        FruitRequestDTO request = new FruitRequestDTO("Apple", 10, 1L);

        when(fruitRepository.findById(fruitId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> fruitService.updateFruit(fruitId, request))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Fruit with id 999 not found");
    }

    @Test
    void updateFruit_WithNonExistentProviderId_ThrowsResourceNotFoundException() {
        Long fruitId = 1L;
        FruitRequestDTO request = new FruitRequestDTO("Apple", 10, 999L);

        Provider provider = new Provider(1L, "Fruits Inc", "Spain");
        Fruit existingFruit = new Fruit(fruitId, "Apple", 10, provider);

        when(fruitRepository.findById(fruitId)).thenReturn(Optional.of(existingFruit));
        when(providerRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> fruitService.updateFruit(fruitId, request))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Provider with id 999 not found");
    }

    @Test
    void updateFruit_WithSameProvider_UpdatesSuccessfully() {
        Long fruitId = 1L;
        FruitRequestDTO request = new FruitRequestDTO("Updated Apple", 15, 1L);

        Provider provider = new Provider(1L, "Fruits Inc", "Spain");
        Fruit existingFruit = new Fruit(fruitId, "Apple", 10, provider);
        Fruit updatedFruit = new Fruit(fruitId, "Updated Apple", 15, provider);

        ProviderResponseDTO providerResponse = new ProviderResponseDTO(1L, "Fruits Inc", "Spain");
        FruitResponseDTO expectedResponse = new FruitResponseDTO(fruitId, "Updated Apple", 15, providerResponse);

        when(fruitRepository.findById(fruitId)).thenReturn(Optional.of(existingFruit));
        when(providerRepository.findById(1L)).thenReturn(Optional.of(provider));
        when(fruitRepository.save(any(Fruit.class))).thenReturn(updatedFruit);
        when(fruitMapper.toResponseDTO(updatedFruit)).thenReturn(expectedResponse);


        FruitResponseDTO result = fruitService.updateFruit(fruitId, request);
        assertThat(result.name()).isEqualTo("Updated Apple");
        assertThat(result.weightInKilos()).isEqualTo(15);
        assertThat(result.provider().id()).isEqualTo(1L);
    }
}
