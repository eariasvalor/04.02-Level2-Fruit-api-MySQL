package cat.itacademy.s04.t02.n02.fruit.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record FruitRequestDTO(
        @NotBlank(message = "Name cannot be blank")
        String name,

        @Positive(message = "Weight must be positive")
        int weightInKilos,

        @NotNull(message = "Provider ID cannot be null")
        Long providerId
) {}
