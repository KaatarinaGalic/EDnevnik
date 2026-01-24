package ba.sum.fpmoz.studentiocjene.controller;

import ba.sum.fpmoz.studentiocjene.models.Course;
import ba.sum.fpmoz.studentiocjene.repository.CourseRepository;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/courses")
@SecurityRequirement(name = "bearerAuth")
public class CourseController {

    @Autowired
    private CourseRepository courseRepository;

    // ADMIN – kreira kolegij
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public Course createCourse(@RequestBody Course course) {
        return courseRepository.save(course);
    }

    // SVI – dohvat svih kolegija
    @GetMapping
    public Iterable<Course> getAllCourses() {
        return courseRepository.findAll();
    }

    // ADMIN – brisanje kolegija
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public void deleteCourse(@PathVariable Long id) {
        courseRepository.deleteById(id);
    }
}
