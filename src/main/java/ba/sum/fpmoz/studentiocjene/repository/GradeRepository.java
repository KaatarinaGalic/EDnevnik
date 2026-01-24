package ba.sum.fpmoz.studentiocjene.repository;

import ba.sum.fpmoz.studentiocjene.models.Grade;
import ba.sum.fpmoz.studentiocjene.models.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface GradeRepository extends JpaRepository<Grade, Long> {

    // USER (student) vidi svoje ocjene
    List<Grade> findByUser(User user);
}
