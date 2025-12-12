package cat.itacademy.s04.t02.n02.fruit.controller;

import cat.itacademy.s04.t02.n02.fruit.dto.ProviderRequestDTO;
import cat.itacademy.s04.t02.n02.fruit.dto.ProviderResponseDTO;
import cat.itacademy.s04.t02.n02.fruit.service.ProviderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/providers")
@RequiredArgsConstructor
public class ProviderController {

    private final ProviderService providerService;

    @PostMapping
    public ResponseEntity<ProviderResponseDTO> createProvider(
            @Valid @RequestBody ProviderRequestDTO request) {
        ProviderResponseDTO response = providerService.createProvider(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<List<ProviderResponseDTO>> getAllProviders() {
        List<ProviderResponseDTO> providers = providerService.getAllProviders();
        return ResponseEntity.ok(providers);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProviderResponseDTO> getProviderById(@PathVariable Long id) {
        ProviderResponseDTO response = providerService.getProviderById(id);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProviderResponseDTO> updateProvider(
            @PathVariable Long id,
            @Valid @RequestBody ProviderRequestDTO request) {
        ProviderResponseDTO response = providerService.updateProvider(id, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProvider(@PathVariable Long id) {
        providerService.deleteProvider(id);
        return ResponseEntity.noContent().build();
    }
}