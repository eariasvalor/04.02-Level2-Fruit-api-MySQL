package cat.itacademy.s04.t02.n02.fruit.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "fruits")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
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
}
