package ba.sum.fpmoz.studentiocjene.controller;

import ba.sum.fpmoz.studentiocjene.dto.ChangePasswordRequest;
import ba.sum.fpmoz.studentiocjene.models.Role;
import ba.sum.fpmoz.studentiocjene.models.User;
import ba.sum.fpmoz.studentiocjene.repository.RoleRepository;
import ba.sum.fpmoz.studentiocjene.repository.UserRepository;
import ba.sum.fpmoz.studentiocjene.services.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;
@Tag(
        name = "User Controller",
        description = "Operacije nad korisnicima. Neke rute su dostupne samo ADMIN korisnicima."
)
@SecurityRequirement(name = "bearerAuth")
@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UserService userService;


    @Operation(
            summary = "Dohvati korisnika po ID-u",
            description = "Samo ADMIN korisnik može dohvatiti korisnika po ID-u"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Korisnik pronađen"),
            @ApiResponse(responseCode = "404", description = "Korisnik nije pronađen"),
            @ApiResponse(responseCode = "403", description = "Zabranjen pristup")
    })
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<User> getUserById(@PathVariable Long id){
        Optional<User> user = userRepository.findById(id);
        return user.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(404).build());
    }

    @Operation(
            summary = "Dohvati sve korisnike",
            description = "Samo ADMIN korisnik može dohvatiti listu svih korisnika"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Lista korisnika"),
            @ApiResponse(responseCode = "403", description = "Zabranjen pristup")
    })
    @GetMapping("/all")
    @PreAuthorize("hasRole('ADMIN')")
    public Iterable<User> getAllUsers() {
        return userRepository.findAll();
    }


    @GetMapping("/me")
    public ResponseEntity<User> getCurrentUser() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (principal instanceof org.springframework.security.core.userdetails.User userDetails) {
            String email = userDetails.getUsername();
            User user = userRepository.findByEmail(email)
                    .orElseThrow(() -> new RuntimeException("Korisnik nije pronađen"));
            return ResponseEntity.ok(user);
        } else {
            return ResponseEntity.status(401).build();
        }
    }

    @Operation(
            summary = "Promjena lozinke",
            description = "Autentificirani korisnik mijenja lozinku unosom stare i nove"
    )
    @PutMapping("/change-password")
    public ResponseEntity<?> changePassword(
            @RequestBody ChangePasswordRequest request,
            Authentication authentication
    ) {
        String email = authentication.getName(); // dolazi iz JWT-a

        userService.changePassword(
                email,
                request.getOldPassword(),
                request.getNewPassword()
        );

        return ResponseEntity.ok("Lozinka uspješno promijenjena");
    }

    @Operation(
            summary = "Brisanje korisničkog računa",
            description = "Samo ADMIN može obrisati korisnika po ID-u. Admin ne može obrisati sam sebe."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Korisnik uspješno obrisan"),
            @ApiResponse(responseCode = "400", description = "Admin ne može obrisati sam sebe"),
            @ApiResponse(responseCode = "404", description = "Korisnik nije pronađen"),
            @ApiResponse(responseCode = "403", description = "Zabranjen pristup")
    })
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> deleteUser(@PathVariable Long id, Authentication authentication) {

        // email trenutno ulogiranog admina (iz JWT-a)
        String adminEmail = authentication.getName();

        User admin = userRepository.findByEmail(adminEmail)
                .orElseThrow(() -> new RuntimeException("Admin nije pronađen"));

        // admin ne smije obrisati sam sebe
        if (admin.getId().equals(id)) {
            return ResponseEntity
                    .badRequest()
                    .body("Admin ne može obrisati sam sebe");
        }

        // provjera postoji li korisnik
        if (!userRepository.existsById(id)) {
            return ResponseEntity
                    .status(404)
                    .body("Korisnik nije pronađen");
        }

        userRepository.deleteById(id);
        return ResponseEntity.ok("Korisnik uspješno obrisan");
    }


}