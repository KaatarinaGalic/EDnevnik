package ba.sum.fpmoz.studentiocjene.repository;

import ba.sum.fpmoz.studentiocjene.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User,Integer> {
    Optional<User> findByName(String name);

    Optional<Object> findByEmail(String email);
}
