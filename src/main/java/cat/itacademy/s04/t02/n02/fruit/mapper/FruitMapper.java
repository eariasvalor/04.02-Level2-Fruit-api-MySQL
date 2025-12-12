package cat.itacademy.s04.t02.n02.fruit.mapper;

import cat.itacademy.s04.t02.n02.fruit.dto.FruitRequestDTO;
import cat.itacademy.s04.t02.n02.fruit.dto.FruitResponseDTO;
import cat.itacademy.s04.t02.n02.fruit.model.Fruit;
import cat.itacademy.s04.t02.n02.fruit.model.Provider;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class FruitMapper {

    private final ProviderMapper providerMapper;

    public Fruit toEntity(FruitRequestDTO dto, Provider provider) {
        return new Fruit(dto.name(), dto.weightInKilos(), provider);
    }

    public FruitResponseDTO toResponseDTO(Fruit entity) {
        return new FruitResponseDTO(
                entity.getId(),
                entity.getName(),
                entity.getWeightInKilos(),
                providerMapper.toResponseDTO(entity.getProvider())
        );
    }
}
