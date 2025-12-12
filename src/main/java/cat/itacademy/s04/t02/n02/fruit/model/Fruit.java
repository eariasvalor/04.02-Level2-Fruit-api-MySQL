package cat.itacademy.s04.t02.n02.fruit.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "fruits")
@Getter
@Setter
@NoArgsConstructor
public class Fruit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private int weightInKilos;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "provider_id", nullable = false)
    private Provider provider;

    public Fruit(String name, int weightInKilos, Provider provider) {
        this.name = name;
        this.weightInKilos = weightInKilos;
        this.provider = provider;
    }

    public Fruit(Long id, String name, int weightInKilos, Provider provider) {
        this.id = id;
        this.name = name;
        this.weightInKilos = weightInKilos;
        this.provider = provider;
    }
}