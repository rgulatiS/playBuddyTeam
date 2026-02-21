package pro.play.venue.model;

import jakarta.persistence.*;
import lombok.*;
import pro.play.audit.AuditModel;
import pro.play.city.model.City;
import pro.play.user.model.User;
import pro.play.court.model.Court;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "venues")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@com.fasterxml.jackson.annotation.JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
public class Venue extends AuditModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String address;
    private String imageUrl;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "city_id")
    private City city;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id")
    @com.fasterxml.jackson.annotation.JsonIgnore
    private User owner;

    @OneToMany(mappedBy = "venue", cascade = CascadeType.ALL)
    @Builder.Default
    @com.fasterxml.jackson.annotation.JsonIgnore
    private List<Court> courts = new ArrayList<>();
}
