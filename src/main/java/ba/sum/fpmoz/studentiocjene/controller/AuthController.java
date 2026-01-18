package ba.sum.fpmoz.studentiocjene.controller;

import ba.sum.fpmoz.studentiocjene.models.Role;
import ba.sum.fpmoz.studentiocjene.models.User;
import ba.sum.fpmoz.studentiocjene.repository.RoleRepository;
import ba.sum.fpmoz.studentiocjene.repository.UserRepository;
import ba.sum.fpmoz.studentiocjene.security.JWTUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/auth")
@Tag(name = "Authentication", description = "Registracija, prijava i JWT tokeni")
public class AuthController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JWTUtil jwtUtil;

    @Operation(
            summary = "Registracija novog korisnika",
            description = "Kreira novog korisnika sa defaultnom USER rolom. Lozinka se automatski hashira."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Korisnik uspješno registriran",
                    content = @Content(schema = @Schema(implementation = User.class))),
            @ApiResponse(responseCode = "409", description = "Email već postoji"),
            @ApiResponse(responseCode = "500", description = "Greška na serveru")
    })
    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody User user) {

        if (userRepository.findByEmail(user.getEmail()).isPresent()) {
            return ResponseEntity.status(409).body("Korisnik s tim emailom već postoji.");
        }

        user.setPassword(passwordEncoder.encode(user.getPassword()));

        Role role = roleRepository.findByName("USER")
                .orElseThrow(() -> new RuntimeException("Greška: Uloga USER nije pronađena u bazi!"));

        user.getRoles().add(role);

        User savedUser = userRepository.save(user);
        return ResponseEntity.ok(savedUser);
    }

    @Operation(
            summary = "Prijava korisnika",
            description = "Provjerava email i lozinku te vraća JWT access i refresh token."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Uspješna prijava"),
            @ApiResponse(responseCode = "401", description = "Neispravni podaci")
    })
    @PostMapping("/login")
    public ResponseEntity<?> login(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(content = @Content(
                    examples = @ExampleObject(
                            value = "{\"email\": \"Kaca@example.com\", \"password\": \"password123\"}"
                    )
            ))
            @RequestBody Map<String, String> userData) {

        String email = userData.get("email");
        String password = userData.get("password");

        // Provjera korisnika
        User user = userRepository.findByEmail(email).orElse(null);
        if (user == null || !passwordEncoder.matches(password, user.getPassword())) {
            return ResponseEntity.status(401).body("Neispravni email ili lozinka.");
        }

        // Dohvati uloge korisnika
        List<String> roles = user.getRoles().stream()
                .map(Role::getName)
                .collect(Collectors.toList());

        // Generiraj token
        String accessToken = jwtUtil.generateToken(user.getEmail(), roles);
        String refreshToken = jwtUtil.generateRefreshToken();

        // Spremi refresh token
        user.setRefreshToken(refreshToken);
        userRepository.save(user);

        return ResponseEntity.ok(Map.of(
                "accessToken", accessToken,
                "refreshToken", refreshToken
        ));
    }

    @Operation(
            summary = "Osvježavanje access tokena",
            description = "Na temelju važećeg refresh tokena generira novi JWT access token."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Token osvježen"),
            @ApiResponse(responseCode = "401", description = "Nevažeći refresh token")
    })
    @PostMapping("/refresh")
    public ResponseEntity<?> refreshToken(@RequestParam String refreshToken) {
        User user = userRepository.findByRefreshToken(refreshToken).orElse(null);

        if (user == null) {
            return ResponseEntity.status(401).body("Nevažeći refresh token.");
        }

        List<String> roles = user.getRoles().stream()
                .map(Role::getName)
                .collect(Collectors.toList());

        String newAccessToken = jwtUtil.generateToken(user.getEmail(), roles);

        return ResponseEntity.ok(Map.of(
                "accessToken", newAccessToken
        ));
    }
}