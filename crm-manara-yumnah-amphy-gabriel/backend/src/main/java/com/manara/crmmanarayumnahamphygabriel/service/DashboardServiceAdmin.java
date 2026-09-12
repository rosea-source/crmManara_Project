package com.manara.crmmanarayumnahamphygabriel.service;

import com.manara.crmmanarayumnahamphygabriel.model.Session;
import com.manara.crmmanarayumnahamphygabriel.repository.ActiviteRepository;
import com.manara.crmmanarayumnahamphygabriel.repository.AnimateurRepository;
import com.manara.crmmanarayumnahamphygabriel.repository.SessionRepository;
import com.manara.crmmanarayumnahamphygabriel.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class DashboardServiceAdmin {

    private final UserRepository userRepository;
    private final ActiviteRepository activiteRepository;
    private final SessionRepository sessionRepository;
    private final AnimateurRepository animateurRepository;

    public DashboardServiceAdmin(UserRepository userRepository,
                            ActiviteRepository activiteRepository,
                            SessionRepository sessionRepository,
                            AnimateurRepository animateurRepository) {
        this.userRepository = userRepository;
        this.activiteRepository = activiteRepository;
        this.sessionRepository = sessionRepository;
        this.animateurRepository = animateurRepository;
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

    public long countPrevues() {
        return sessionRepository.countByStatut("Prévue");
    }

    public long countAnnulees() {
        return sessionRepository.countByStatut("Annulée");
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

    public LocalDateTime lastUpdate() {
        return LocalDateTime.now();
    }
}