package ba.sum.fpmoz.studentiocjene.repository;

import ba.sum.fpmoz.studentiocjene.models.Course;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CourseRepository extends JpaRepository<Course, Long> {
}
