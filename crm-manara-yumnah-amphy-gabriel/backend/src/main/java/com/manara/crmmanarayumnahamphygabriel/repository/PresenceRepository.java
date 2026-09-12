package com.manara.crmmanarayumnahamphygabriel.repository;

import com.manara.crmmanarayumnahamphygabriel.model.Presence;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PresenceRepository extends JpaRepository<Presence, Integer> {

    @Query("SELECT p FROM Presence p " +
            "JOIN FETCH p.inscription i " +
            "JOIN FETCH i.enfant e " +
            "JOIN FETCH i.session s " +
            "JOIN FETCH s.activite " +
            "WHERE s.animateur.id = :animateurId " +
            "ORDER BY p.id DESC")
    List<Presence> findByAnimateurId(@Param("animateurId") Integer animateurId);

    @Query("SELECT p FROM Presence p " +
            "JOIN FETCH p.inscription i " +
            "JOIN FETCH i.enfant e " +
            "JOIN FETCH i.session s " +
            "JOIN FETCH s.activite " +
            "WHERE i.session.id = :sessionId " +
            "ORDER BY e.nom ASC")
    List<Presence> findBySessionId(@Param("sessionId") Integer sessionId);

    boolean existsByInscriptionId(Integer inscriptionId);
}