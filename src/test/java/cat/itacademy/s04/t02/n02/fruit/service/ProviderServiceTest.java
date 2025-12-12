package cat.itacademy.s04.t02.n02.fruit.service;

import cat.itacademy.s04.t02.n02.fruit.dto.ProviderRequestDTO;
import cat.itacademy.s04.t02.n02.fruit.dto.ProviderResponseDTO;
import cat.itacademy.s04.t02.n02.fruit.exception.DuplicateResourceException;
import cat.itacademy.s04.t02.n02.fruit.mapper.ProviderMapper;
import cat.itacademy.s04.t02.n02.fruit.model.Provider;
import cat.itacademy.s04.t02.n02.fruit.repository.ProviderRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

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
}
