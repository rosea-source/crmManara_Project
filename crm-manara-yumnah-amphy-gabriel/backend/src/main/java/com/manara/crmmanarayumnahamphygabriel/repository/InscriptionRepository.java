package com.manara.crmmanarayumnahamphygabriel.repository;

import com.manara.crmmanarayumnahamphygabriel.model.Enfant;
import com.manara.crmmanarayumnahamphygabriel.model.Inscription;
import com.manara.crmmanarayumnahamphygabriel.model.Parent;
import com.manara.crmmanarayumnahamphygabriel.model.Session;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface InscriptionRepository extends JpaRepository<Inscription,Integer> {
    long countByEnfant(Enfant enfant);
    List<Inscription> findByStatutPaiement(String statutPaiement);
    List<Inscription> findBySession(Session session);
    long countBySession(Session session);
    List<Inscription> findByEnfant(Enfant enfant);

    List<Inscription> findByEnfantParent(Parent parent);
    List<Inscription> findByEnfantParentAndSessionDateDebutAfter(
            Parent parent,
            LocalDate date
    );
    boolean existsBySessionIdAndEnfantId(Integer sessionId, Integer enfantId);
    List<Inscription> findBySessionAndStatutPaiement(Session session, String statut);

}
