package cat.itacademy.s04.t02.n02.fruit.service;

import cat.itacademy.s04.t02.n02.fruit.dto.FruitRequestDTO;
import cat.itacademy.s04.t02.n02.fruit.dto.FruitResponseDTO;

import java.util.List;

public interface FruitService {
    FruitResponseDTO createFruit(FruitRequestDTO request);
    List<FruitResponseDTO> getFruitsByProviderId(Long providerId);
    List<FruitResponseDTO> getAllFruits();
    FruitResponseDTO getFruitById(Long id);
}