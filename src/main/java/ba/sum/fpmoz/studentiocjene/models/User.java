package ba.sum.fpmoz.studentiocjene.models;

import ba.sum.fpmoz.studentiocjene.models.Role;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "users")
@Schema(
        name = "User",
        description = "Model koji predstavlja korisnika sustava"
)
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(
            description = "Jedinstveni identifikator korisnika",
            example = "1",
            accessMode = Schema.AccessMode.READ_ONLY
    )
    private Long id;

    @Column(nullable = false)
    @Schema(
            description = "Ime korisnika",
            example = "Katarina"
    )
    private String name;

    @Column(nullable = false)
    @Schema(
            description = "Prezime korisnika",
            example = "Galic"
    )
    private String lastname;

    @Column(nullable = false, unique = true)
    @Schema(
            description = "Email adresa korisnika (koristi se za login)",
            example = "Kataa@test.com"
    )
    private String email;

    @Column(nullable = false)
    @Schema(
            description = "Lozinka korisnika (BCrypt hash)",
            example = "password123"
    )
    private String password;

    @Column
    @JsonIgnore
    @Schema(
            description = "Refresh token za JWT autentikaciju",
            accessMode = Schema.AccessMode.READ_ONLY
    )
    private String refreshToken;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "user_roles",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    @JsonIgnoreProperties("users")
    @Schema(
            description = "Uloge dodijeljene korisniku",
            accessMode = Schema.AccessMode.READ_ONLY
    )
    private Set<Role> roles = new HashSet<>();

    public User() {}

    public User(String name, String lastname, String email, String password) {
        this.name = name;
        this.lastname = lastname;
        this.email = email;
        this.password = password;
    }

    public User(String name, String lastname, String email, String password, Set<Role> roles) {
        this.name = name;
        this.lastname = lastname;
        this.email = email;
        this.password = password;
        this.roles = roles;
    }

    // Getteri i setteri

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

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Set<Role> getRoles() {
        return roles;
    }

    public void setRoles(Set<Role> roles) {
        this.roles = roles;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }
}