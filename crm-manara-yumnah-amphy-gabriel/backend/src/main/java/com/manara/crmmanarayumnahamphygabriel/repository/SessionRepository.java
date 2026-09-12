package com.manara.crmmanarayumnahamphygabriel.repository;

import com.manara.crmmanarayumnahamphygabriel.model.Activite;
import com.manara.crmmanarayumnahamphygabriel.model.Session;
import com.manara.crmmanarayumnahamphygabriel.model.Animateur;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.Optional;
import java.util.List;

@Repository
public interface SessionRepository extends JpaRepository<Session,Integer> {
    List<Session> findByAnimateur(Animateur animateur);
    List<Session> findByActivite(Activite activite);

    List<Session> findByActiviteId(Integer id);
    List<Session> findAllByOrderByDateDebutAscHeureDebutAsc();
    long countByStatut(String statut);

    @Query("SELECT s FROM Session s " +
            "JOIN FETCH s.activite " +
            "JOIN FETCH s.animateur a " +
            "JOIN FETCH a.user " +
            "WHERE s.id = :id")
    Optional<Session> findByIdWithDetails(@Param("id") Integer id);

}