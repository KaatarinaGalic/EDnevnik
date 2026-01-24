package ba.sum.fpmoz.studentiocjene.models;

import jakarta.persistence.*;

@Entity
public class Course {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private String description;

    public Course() {}

    public Course(String name, String description) {
        this.name = name;
        this.description = description;
    }

    // GETTERI I SETTERI
    public Long getId() { return id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
}
