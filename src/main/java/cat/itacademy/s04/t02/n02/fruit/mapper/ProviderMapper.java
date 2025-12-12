package cat.itacademy.s04.t02.n02.fruit.mapper;

import cat.itacademy.s04.t02.n02.fruit.dto.ProviderRequestDTO;
import cat.itacademy.s04.t02.n02.fruit.dto.ProviderResponseDTO;
import cat.itacademy.s04.t02.n02.fruit.model.Provider;
import org.springframework.stereotype.Component;

@Component
public class ProviderMapper {

    public Provider toEntity(ProviderRequestDTO dto) {
        return new Provider(null, dto.name(), dto.country());
    }

    public ProviderResponseDTO toResponseDTO(Provider entity) {
        return new ProviderResponseDTO(entity.getId(), entity.getName(), entity.getCountry());
    }
}