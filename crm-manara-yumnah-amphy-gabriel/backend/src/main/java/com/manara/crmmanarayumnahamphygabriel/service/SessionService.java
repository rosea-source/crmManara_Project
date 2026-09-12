package com.manara.crmmanarayumnahamphygabriel.service;

import com.manara.crmmanarayumnahamphygabriel.model.Activite;
import com.manara.crmmanarayumnahamphygabriel.model.Session;
import com.manara.crmmanarayumnahamphygabriel.model.Animateur;
import com.manara.crmmanarayumnahamphygabriel.repository.ActiviteRepository;
import com.manara.crmmanarayumnahamphygabriel.repository.SessionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.time.DayOfWeek;
import java.util.stream.Collectors;

@Service
public class SessionService {

    @Autowired
    private SessionRepository sessionRepository;
    @Autowired
    private ActiviteRepository activiteRepository;

    public List<Session> getAll() {
        return sessionRepository.findAll();
    }


    public Session getById(Integer id) {
        return sessionRepository.findByIdWithDetails(id).orElse(null);
    }
    public Session save(Session session) {
        return sessionRepository.save(session);
    }

    public void delete(Integer id) {
        sessionRepository.deleteById(id);
    }

    public List<Session> getByAnimateur(Animateur animateur){
        return sessionRepository.findByAnimateur(animateur);
    }
    public List<Session> getByActivite(Activite activite){
        return sessionRepository.findByActivite(activite);
    }

    public Activite getActiviteById(Integer id) {
        return activiteRepository.findById(id).orElse(null);
    }

    public List<Session> getByActiviteId(Integer id) {
        return sessionRepository.findByActiviteId(id);
    }
    public List<Session> getAllSessions() {
        return sessionRepository.findAllByOrderByDateDebutAscHeureDebutAsc();
    }
    public List<Session> getSessionsByDay(DayOfWeek day) {
        return sessionRepository.findAllByOrderByDateDebutAscHeureDebutAsc()
                .stream()
                .filter(s -> s.getDateDebut().getDayOfWeek().equals(day))
                .collect(Collectors.toList());
    }

    public long countSessions() {
        return sessionRepository.count();
    }

    public long countCancelledSessions() {
        return sessionRepository.countByStatut("Annulée");
    }

    public long countActiveSessions() {
        return sessionRepository.countByStatut("Prévue");
    }


}