package com.manara.crmmanarayumnahamphygabriel.service;

import com.manara.crmmanarayumnahamphygabriel.repository.*;
import org.springframework.stereotype.Service;
import com.manara.crmmanarayumnahamphygabriel.model.Session;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class AdminDashboardService {

    private final UserRepository userRepository;
    private final ActiviteRepository activiteRepository;
    private final SessionRepository sessionRepository;
    private final AnimateurRepository animateurRepository;
    private final EnfantRepository  enfantRepository;
    public AdminDashboardService(UserRepository userRepository,
                                 ActiviteRepository activiteRepository,
                                 SessionRepository sessionRepository,
                                 AnimateurRepository animateurRepository,
                                 EnfantRepository enfantRepository) {
        this.userRepository = userRepository;
        this.activiteRepository = activiteRepository;
        this.sessionRepository = sessionRepository;
        this.animateurRepository = animateurRepository;
        this.enfantRepository = enfantRepository;
    }

    public long countUsers() {
        return userRepository.count();
    }

    public long countActivities() {
        return activiteRepository.count();
    }

    public long countSessions() {
        return sessionRepository.count();
    }

    public long countAnimateurs() {
        return animateurRepository.count();
    }

    public LocalDateTime lastUpdate() {
        return LocalDateTime.now();
    }
    public long countPrevues() {
        return sessionRepository.countByStatut("Prévue");
    }

    public long countAnnulees() {
        return sessionRepository.countByStatut("Annulée");
    }
    public long countEnCours() {
        return sessionRepository.countByStatut("En cours");
    }

    public long countTerminees() {
        return sessionRepository.countByStatut("Terminée");
    }
    public List<Session> getRecentSessions() {
        return sessionRepository.findAllByOrderByDateDebutAscHeureDebutAsc()
                .stream()
                .limit(5)
                .toList();
    }
    public long countEnfants() {
        return enfantRepository.count();
    }


    public long countParents() {
        return userRepository.countByRole("PARENT");
    }
}