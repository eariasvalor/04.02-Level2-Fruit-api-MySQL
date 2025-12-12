package cat.itacademy.s04.t02.n02.fruit.service;

import cat.itacademy.s04.t02.n02.fruit.dto.ProviderRequestDTO;
import cat.itacademy.s04.t02.n02.fruit.dto.ProviderResponseDTO;
import cat.itacademy.s04.t02.n02.fruit.exception.DuplicateResourceException;
import cat.itacademy.s04.t02.n02.fruit.mapper.ProviderMapper;
import cat.itacademy.s04.t02.n02.fruit.model.Provider;
import cat.itacademy.s04.t02.n02.fruit.repository.ProviderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ProviderServiceImpl implements ProviderService {
    private static final String DUPLICATED_PROVIDER_MESSAGE = "Provider with name '%s' already exists";

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
}
