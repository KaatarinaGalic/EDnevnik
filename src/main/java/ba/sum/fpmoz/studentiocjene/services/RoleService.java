package ba.sum.fpmoz.studentiocjene.services;

import ba.sum.fpmoz.studentiocjene.models.Role;
import ba.sum.fpmoz.studentiocjene.repository.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RoleService {

    @Autowired
    private RoleRepository roleRepository;

    public Role createRole(String roleName) {
        if(roleRepository.findByName(roleName).isPresent()) {
            throw new RuntimeException("Uloga već postoji!");
        }

        Role role = new Role();
        role.setName(roleName.toUpperCase());
        return roleRepository.save(role);
    }

    public Role getRoleByName(String name) {
        return roleRepository.findByName(name).orElse(null);
    }
}