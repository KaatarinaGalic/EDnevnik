package ba.sum.fpmoz.studentiocjene.repository;

import ba.sum.fpmoz.studentiocjene.models.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> { // Popravljena zagrada >
    Optional<Role> findByName(String name);
}
