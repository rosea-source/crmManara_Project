package com.manara.crmmanarayumnahamphygabriel.service;

import com.manara.crmmanarayumnahamphygabriel.model.Enfant;
import com.manara.crmmanarayumnahamphygabriel.model.Inscription;
import com.manara.crmmanarayumnahamphygabriel.model.Session;
import com.manara.crmmanarayumnahamphygabriel.repository.InscriptionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class InscriptionService {

    @Autowired
    private NotificationService notificationService;
    @Autowired
    InscriptionRepository inscriptionRepository;

    public List<Inscription> getAll()
    {
        return inscriptionRepository.findAll();
    }
    public List<Inscription> getEnAttente() {
        return inscriptionRepository.findByStatutPaiement("En attente");
    }

    public Inscription save(Inscription inscription)
    {
        return inscriptionRepository.save(inscription);
    }
    public void delete(Integer id)
    {
        inscriptionRepository.deleteById(id);
    }

    public List<Inscription> getBySession(Session session){
        return inscriptionRepository.findBySession(session);
    }
    public long countByEnfant(Enfant enfant){
        return inscriptionRepository.countByEnfant(enfant);
    }
    public long countBySession(Session session){
        return inscriptionRepository.countBySession(session);
    }
    public List<Inscription> getByEnfant(Enfant enfant){
        return inscriptionRepository.findByEnfant(enfant);
    }
    public Inscription getById(Integer id){
        return inscriptionRepository.findById(id).orElse(null);
    }
    public boolean exists(Integer sessionId, Integer enfantId) {
        return inscriptionRepository.existsBySessionIdAndEnfantId(sessionId, enfantId);
    }
    public List<Inscription> getWaitingBySession(Session session) {
        return inscriptionRepository
                .findBySessionAndStatutPaiement(session, "En attente");
    }
    public Inscription accepterInscription(Integer inscriptionId) {

        Inscription ins = inscriptionRepository.findById(inscriptionId)
                .orElseThrow(() -> new RuntimeException("Inscription introuvable"));

        ins.setStatutPaiement("Accepté");
        Inscription saved = inscriptionRepository.save(ins); // ← commit immédiat

        // Email en arrière-plan grâce à @Async — ne bloque plus la réponse HTTP
        Enfant enfant   = ins.getEnfant();
        Session session = ins.getSession();

        if (enfant != null && enfant.getParent() != null) {
            String dateDebut  = session.getDateDebut()  != null ? session.getDateDebut().toString()  : "—";
            String dateFin    = session.getDateFin()    != null ? session.getDateFin().toString()    : "—";
            String heureDebut = session.getHeureDebut() != null ? session.getHeureDebut().toString() : "—";
            String heureFin   = session.getHeureFin()   != null ? session.getHeureFin().toString()   : "—";
            String lieu       = session.getLieu()       != null ? session.getLieu()                  : "";

            notificationService.notifyInscription(
                    enfant.getParent().getUser(),
                    enfant.getPrenom(),
                    session.getActivite().getTitre(),
                    dateDebut, dateFin, heureDebut, heureFin, lieu
            );
        }

        return saved;
    }
    

}

