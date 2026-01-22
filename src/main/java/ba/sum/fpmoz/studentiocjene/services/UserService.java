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
                .orElseThrow(() -> new RuntimeException("Uloga " + roleName + " ne postoji u bazi podataka!"));


        User user = new User();
        user.setName(name);
        user.setLastname(lastname);
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(password));

        // Postavljanje uloge
        user.getRoles().add(role);

        return userRepository.save(user);
    }

    // Dohvat korisnika po emaiilu
    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public void changePassword(String email, String oldPassword, String newPassword) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Korisnik ne postoji"));

        // provjera stare lozinke
        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            throw new RuntimeException("Stara lozinka nije ispravna");
        }

        // hash nove lozinke
        user.setPassword(passwordEncoder.encode(newPassword));

        userRepository.save(user);
    }

}