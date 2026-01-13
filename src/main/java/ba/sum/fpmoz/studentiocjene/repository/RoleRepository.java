package ba.sum.fpmoz.studentiocjene.repository;

import ba.sum.fpmoz.studentiocjene.models.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role,Integer> {
    Optional<Role> findByName(String name);
}
