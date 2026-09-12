package com.manara.crmmanarayumnahamphygabriel.repository;

import com.manara.crmmanarayumnahamphygabriel.model.Enfant;
import com.manara.crmmanarayumnahamphygabriel.model.MessageEnfant;
import com.manara.crmmanarayumnahamphygabriel.model.Session;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MessageEnfantRepository extends JpaRepository<MessageEnfant, Integer> {

    @Query("SELECT m FROM MessageEnfant m " +
            "JOIN FETCH m.session s " +
            "JOIN FETCH s.activite " +
            "LEFT JOIN FETCH m.enfant " +
            "LEFT JOIN FETCH m.animateur a " +
            "LEFT JOIN FETCH a.user " +
            "WHERE s = :session " +
            "ORDER BY m.dateEnvoi DESC")
    List<MessageEnfant> findBySessionOrderByDateEnvoiDesc(@Param("session") Session session);

    @Query("SELECT m FROM MessageEnfant m " +
            "JOIN FETCH m.session s " +
            "JOIN FETCH s.activite " +
            "LEFT JOIN FETCH m.enfant " +
            "LEFT JOIN FETCH m.animateur a " +
            "LEFT JOIN FETCH a.user " +
            "WHERE s = :session " +
            "AND (m.enfant = :enfant OR m.enfant IS NULL) " +
            "ORDER BY m.dateEnvoi DESC")
    List<MessageEnfant> findBySessionAndEnfantOrGlobal(
            @Param("session") Session session,
            @Param("enfant") Enfant enfant
    );

    @Query("SELECT m FROM MessageEnfant m " +
            "JOIN FETCH m.session s " +
            "JOIN FETCH s.activite " +
            "LEFT JOIN FETCH m.enfant " +
            "LEFT JOIN FETCH m.animateur a " +
            "LEFT JOIN FETCH a.user " +
            "WHERE (m.enfant = :enfant OR m.enfant IS NULL) " +
            "AND s IN :sessions " +
            "ORDER BY m.dateEnvoi DESC")
    List<MessageEnfant> findAllForEnfant(
            @Param("enfant") Enfant enfant,
            @Param("sessions") List<Session> sessions
    );

    long countByEnfantAndLuFalse(Enfant enfant);
}