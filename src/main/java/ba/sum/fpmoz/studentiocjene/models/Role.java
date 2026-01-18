package ba.sum.fpmoz.studentiocjene.models;


import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "roles")
@Schema(
        name = "Role",
        description = "Model koji predstavlja korisničku ulogu u sustavu (npr. USER, ADMIN)"
)
public class Role {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(
            description = "Jedinstveni identifikator uloge",
            example = "1",
            accessMode = Schema.AccessMode.READ_ONLY
    )
    private Long id;

    @Column(nullable = false, unique = true)
    @Schema(
            description = "Naziv uloge",
            example = "ADMIN",
            allowableValues = {"USER", "ADMIN"}
    )
    private String name;

    @ManyToMany(mappedBy = "roles")
    @Schema(
            description = "Korisnici koji imaju ovu ulogu",
            accessMode = Schema.AccessMode.READ_ONLY
    )
    private Set<User> users = new HashSet<>();

    public Role() {}

    public Role(String name) {
        this.name = name;
    }

    // getters & setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Set<User> getUsers() {
        return users;
    }

    public void setUsers(Set<User> users) {
        this.users = users;
    }
}