package cat.itacademy.s04.t02.n02.fruit.repository;

import cat.itacademy.s04.t02.n02.fruit.model.Fruit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FruitRepository extends JpaRepository<Fruit, Long> {
    boolean existsByProviderId(Long providerId);
    List<Fruit> findByProviderId(Long providerId);
}
