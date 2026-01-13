package ba.sum.fpmoz.studentiocjene.services;

import ba.sum.fpmoz.studentiocjene.models.Role;
import ba.sum.fpmoz.studentiocjene.models.User;
import ba.sum.fpmoz.studentiocjene.repository.RoleRepository;
import ba.sum.fpmoz.studentiocjene.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // Registracija korisnika s ulogom
    public User registerUser(String name, String lastname, String email, String password, String roleName) {
        if(userRepository.findByEmail(email).isPresent()) {
            throw new RuntimeException("Korisnik već postoji!");
        }

        Role role = roleRepository.findByName(roleName.toUpperCase())
                .orElseThrow(() -> new RuntimeException("Uloga ne postoji"));

        User user = new User(name, lastname, email, passwordEncoder.encode(password));
        user.setRoles(Collections.singleton(role));

        return userRepository.save(user);
    }

    // Dohvat korisnika po emailu
    public Optional<Object> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }
}
