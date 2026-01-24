package ba.sum.fpmoz.studentiocjene.controller;

import ba.sum.fpmoz.studentiocjene.models.Course;
import ba.sum.fpmoz.studentiocjene.models.Grade;
import ba.sum.fpmoz.studentiocjene.models.User;
import ba.sum.fpmoz.studentiocjene.repository.CourseRepository;
import ba.sum.fpmoz.studentiocjene.repository.GradeRepository;
import ba.sum.fpmoz.studentiocjene.repository.UserRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Grades", description = "Upravljanje ocjenama studenata")
@RestController
@RequestMapping("/grades")
@SecurityRequirement(name = "bearerAuth")
public class GradeController {

    @Autowired
    private GradeRepository gradeRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CourseRepository courseRepository;

    // ADMIN – dodjeljuje ocjenu studentu
    @Operation(summary = "Dodjela ocjene studentu (ADMIN)")
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")

    public ResponseEntity<?> addGrade(
            @RequestParam Long userId,
            @RequestParam Long courseId,
            @RequestParam Integer value
    ) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Korisnik ne postoji"));

        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new RuntimeException("Kolegij ne postoji"));

        Grade grade = new Grade(user, course, value);
        return ResponseEntity.ok(gradeRepository.save(grade));
    }

    // ADMIN – dohvat svih ocjena
    @Operation(summary = "Dohvat svih ocjena (ADMIN)")
    @GetMapping("/all")
    @PreAuthorize("hasRole('ADMIN')")
    public List<Grade> getAllGrades() {
        return gradeRepository.findAll();
    }

    // USER – dohvat svojih ocjena
    @Operation(summary = "Dohvat vlastitih ocjena (USER)")
    @GetMapping("/me")
    public List<Grade> getMyGrades(Authentication authentication) {
        String email = authentication.getName();

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Korisnik ne postoji"));

        return gradeRepository.findByUser(user);
    }

    // ADMIN – izmjena ocjene
    @Operation(summary = "Izmjena ocjene (ADMIN)")
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public Grade updateGrade(
            @PathVariable Long id,
            @RequestParam Integer value
    ) {
        Grade grade = gradeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Ocjena ne postoji"));

        grade.setValue(value);
        return gradeRepository.save(grade);
    }

    // ADMIN – brisanje ocjene
    @Operation(summary = "Brisanje ocjene (ADMIN)")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public void deleteGrade(@PathVariable Long id) {
        gradeRepository.deleteById(id);
    }
}
