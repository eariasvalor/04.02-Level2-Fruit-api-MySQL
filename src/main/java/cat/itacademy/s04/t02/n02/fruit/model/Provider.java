package cat.itacademy.s04.t02.n02.fruit.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "providers")
@Getter
@Setter
@NoArgsConstructor
public class Provider {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    @Column(nullable = false)
    private String country;

    @OneToMany(mappedBy = "provider", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Fruit> fruits = new ArrayList<>();

    public Provider(Long id, String name, String country) {
        this.id = id;
        this.name = name;
        this.country = country;
        this.fruits = new ArrayList<>();
    }


    public Provider(Long id, String name, String country, List<Fruit> fruits) {
        this.id = id;
        this.name = name;
        this.country = country;
        this.fruits = fruits != null ? fruits : new ArrayList<>();
    }
}
