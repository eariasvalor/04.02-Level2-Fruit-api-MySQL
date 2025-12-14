package cat.itacademy.s04.t02.n02.fruit.service;

import cat.itacademy.s04.t02.n02.fruit.dto.FruitRequestDTO;
import cat.itacademy.s04.t02.n02.fruit.dto.FruitResponseDTO;
import cat.itacademy.s04.t02.n02.fruit.exception.ResourceNotFoundException;
import cat.itacademy.s04.t02.n02.fruit.mapper.FruitMapper;
import cat.itacademy.s04.t02.n02.fruit.model.Fruit;
import cat.itacademy.s04.t02.n02.fruit.model.Provider;
import cat.itacademy.s04.t02.n02.fruit.repository.FruitRepository;
import cat.itacademy.s04.t02.n02.fruit.repository.ProviderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FruitServiceImpl implements FruitService {

    private static final String PROVIDER_NOT_FOUND_MESSAGE = "Provider with id %d not found";

    private final FruitRepository fruitRepository;
    private final ProviderRepository providerRepository;
    private final FruitMapper fruitMapper;

    @Override
    @Transactional
    public FruitResponseDTO createFruit(FruitRequestDTO request) {
        Provider provider = providerRepository.findById(request.providerId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        String.format(PROVIDER_NOT_FOUND_MESSAGE, request.providerId())
                ));

        Fruit fruit = fruitMapper.toEntity(request, provider);
        Fruit savedFruit = fruitRepository.save(fruit);
        return fruitMapper.toResponseDTO(savedFruit);
    }

    @Override
    @Transactional(readOnly = true)
    public List<FruitResponseDTO> getFruitsByProviderId(Long providerId) {
        if (!providerRepository.existsById(providerId)) {
            throw new ResourceNotFoundException(
                    String.format(PROVIDER_NOT_FOUND_MESSAGE, providerId)
            );
        }

        return fruitRepository.findByProviderId(providerId)
                .stream()
                .map(fruitMapper::toResponseDTO)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<FruitResponseDTO> getAllFruits() {
        return fruitRepository.findAll()
                .stream()
                .map(fruitMapper::toResponseDTO)
                .toList();
    }


}
