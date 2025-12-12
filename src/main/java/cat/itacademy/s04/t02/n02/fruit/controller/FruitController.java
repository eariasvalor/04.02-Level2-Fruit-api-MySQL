package cat.itacademy.s04.t02.n02.fruit.controller;

import cat.itacademy.s04.t02.n02.fruit.dto.FruitRequestDTO;
import cat.itacademy.s04.t02.n02.fruit.dto.FruitResponseDTO;
import cat.itacademy.s04.t02.n02.fruit.service.FruitService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/fruits")
@RequiredArgsConstructor
public class FruitController {

    private final FruitService fruitService;

    @PostMapping
    public ResponseEntity<FruitResponseDTO> createFruit(
            @Valid @RequestBody FruitRequestDTO request) {
        FruitResponseDTO response = fruitService.createFruit(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
