package cat.itacademy.s04.t02.n02.fruit.service;

import cat.itacademy.s04.t02.n02.fruit.dto.ProviderRequestDTO;
import cat.itacademy.s04.t02.n02.fruit.dto.ProviderResponseDTO;
import cat.itacademy.s04.t02.n02.fruit.exception.DuplicateResourceException;
import cat.itacademy.s04.t02.n02.fruit.exception.ResourceNotFoundException;
import cat.itacademy.s04.t02.n02.fruit.mapper.ProviderMapper;
import cat.itacademy.s04.t02.n02.fruit.model.Provider;
import cat.itacademy.s04.t02.n02.fruit.repository.ProviderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProviderServiceImpl implements ProviderService {
    private static final String DUPLICATED_PROVIDER_MESSAGE = "Provider with name '%s' already exists";
    private static final String PROVIDER_NOT_FOUND_MESSAGE = "Provider with id %s not found";

    private final ProviderRepository providerRepository;
    private final ProviderMapper providerMapper;

    @Override
    @Transactional
    public ProviderResponseDTO createProvider(ProviderRequestDTO request) {
        if (providerRepository.existsByName(request.name())) {
            throw new DuplicateResourceException(String.format(DUPLICATED_PROVIDER_MESSAGE, request.name())
            );
        }

        Provider entity = providerMapper.toEntity(request);
        Provider savedEntity = providerRepository.save(entity);
        return providerMapper.toResponseDTO(savedEntity);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProviderResponseDTO> getAllProviders() {
        return providerRepository.findAll()
                .stream()
                .map(providerMapper::toResponseDTO)
                .toList();
    }

    @Override
    @Transactional
    public ProviderResponseDTO updateProvider(Long id, ProviderRequestDTO request) {
        Provider provider = providerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        String.format(PROVIDER_NOT_FOUND_MESSAGE, id)
                ));

        if (!provider.getName().equals(request.name()) &&
                providerRepository.existsByName(request.name())) {
            throw new DuplicateResourceException(
                    String.format(DUPLICATED_PROVIDER_MESSAGE, request.name())
            );
        }

        provider.setName(request.name());
        provider.setCountry(request.country());

        Provider updatedProvider = providerRepository.save(provider);
        return providerMapper.toResponseDTO(updatedProvider);
    }


}
