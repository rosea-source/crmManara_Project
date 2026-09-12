package com.manara.crmmanarayumnahamphygabriel.api;

import com.manara.crmmanarayumnahamphygabriel.model.Animateur;
import com.manara.crmmanarayumnahamphygabriel.model.Parent;
import com.manara.crmmanarayumnahamphygabriel.model.User;
import com.manara.crmmanarayumnahamphygabriel.repository.AnimateurRepository;
import com.manara.crmmanarayumnahamphygabriel.repository.ParentRepository;
import com.manara.crmmanarayumnahamphygabriel.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/compte")
public class CompteRestController {

    private final UserService userService;
    private final ParentRepository parentRepository;
    private final AnimateurRepository animateurRepository;
    private final PasswordEncoder passwordEncoder;

    public CompteRestController(UserService userService,
                                ParentRepository parentRepository,
                                AnimateurRepository animateurRepository,
                                PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.parentRepository = parentRepository;
        this.animateurRepository = animateurRepository;
        this.passwordEncoder = passwordEncoder;
    }

    // ─── GET /api/compte ─────────────────────────────────────────────────────

    @GetMapping
    public ResponseEntity<CompteDTO> getCompte(
            @AuthenticationPrincipal UserDetails userDetails) {

        User user = userService.findByEmail(userDetails.getUsername());
        if (user == null) return ResponseEntity.notFound().build();

        String role = user.getRole();

        if ("ROLE_ANIMATEUR".equals(role)) {
            Optional<Animateur> optAnim = animateurRepository.findByUserEmail(user.getEmail());
            String diplome      = optAnim.map(Animateur::getDiplome).orElse("");
            String specialite   = optAnim.map(Animateur::getSpecialite).orElse("");
            String dateEmbauche = optAnim
                    .map(a -> a.getDateEmbauche() != null ? a.getDateEmbauche().toString() : "")
                    .orElse("");

            return ResponseEntity.ok(new CompteDTO(
                    user.getPrenom(), user.getNom(), user.getEmail(),
                    user.getTelephone(),          // ← téléphone inclus
                    role, null,
                    diplome, specialite, dateEmbauche));
        }

        // ROLE_PARENT
        Parent parent = parentRepository.findByUserEmail(user.getEmail());
        String adresse = (parent != null && parent.getAdresse() != null)
                ? parent.getAdresse() : "";

        return ResponseEntity.ok(new CompteDTO(
                user.getPrenom(), user.getNom(), user.getEmail(),
                user.getTelephone(),              // ← téléphone inclus
                role, adresse,
                null, null, null));
    }

    // ─── PUT /api/compte/update ───────────────────────────────────────────────

    @PutMapping("/update")
    public ResponseEntity<Void> updateCompte(
            @RequestBody CompteUpdateRequest req,
            @AuthenticationPrincipal UserDetails userDetails) {

        User user = userService.findByEmail(userDetails.getUsername());
        if (user == null) return ResponseEntity.notFound().build();

        // Infos de base
        if (req.prenom() != null && !req.prenom().isBlank())
            user.setPrenom(req.prenom());
        if (req.nom() != null && !req.nom().isBlank())
            user.setNom(req.nom());
        if (req.email() != null && !req.email().isBlank())
            user.setEmail(req.email());

        // ← téléphone : on accepte la valeur même si vide (pour pouvoir l'effacer)
        if (req.telephone() != null)
            user.setTelephone(req.telephone());

        // Mot de passe (seulement si fourni et confirmé)
        if (req.motDePasse() != null && !req.motDePasse().isBlank()
                && req.motDePasse().equals(req.motDePasseConf())) {
            user.setPassword(passwordEncoder.encode(req.motDePasse()));
        }

        userService.save(user);

        // Champs animateur
        if ("ROLE_ANIMATEUR".equals(user.getRole())) {
            Optional<Animateur> optAnim = animateurRepository.findByUserEmail(user.getEmail());
            optAnim.ifPresent(anim -> {
                if (req.diplome() != null)    anim.setDiplome(req.diplome());
                if (req.specialite() != null) anim.setSpecialite(req.specialite());
                animateurRepository.save(anim);
            });
        }

        // Champs parent
        if ("ROLE_PARENT".equals(user.getRole())) {
            Parent parent = parentRepository.findByUserEmail(user.getEmail());
            if (parent != null && req.adresse() != null) {
                parent.setAdresse(req.adresse());
                parentRepository.save(parent);
            }
        }

        return ResponseEntity.ok().build();
    }

    // ═══════════════════════════════════════════════════════════════════════
    // DTOs
    // ═══════════════════════════════════════════════════════════════════════

    public record CompteDTO(
            String prenom,
            String nom,
            String email,
            String telephone,      // ← ajouté
            String role,
            String adresse,        // parent seulement
            String diplome,        // animateur seulement
            String specialite,     // animateur seulement
            String dateEmbauche    // animateur seulement (lecture seule)
    ) {}

    public record CompteUpdateRequest(
            String prenom,
            String nom,
            String email,
            String telephone,      // ← ajouté
            String motDePasse,
            String motDePasseConf,
            String adresse,        // parent
            String diplome,        // animateur
            String specialite      // animateur
    ) {}
}