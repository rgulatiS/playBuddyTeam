package pro.play.city.model;

import jakarta.persistence.*;
import lombok.*;
import pro.play.venue.model.Venue;

import java.util.List;
import java.util.ArrayList;

@Entity
@Table(name = "cities")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class City {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    private String country;

    @OneToMany(mappedBy = "city", cascade = CascadeType.ALL)
    @Builder.Default
    private List<Venue> venues = new ArrayList<>();
}

