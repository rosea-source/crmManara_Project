package com.manara.crmmanarayumnahamphygabriel.repository;

import com.manara.crmmanarayumnahamphygabriel.model.Animateur;
import com.manara.crmmanarayumnahamphygabriel.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
@Repository
public interface AnimateurRepository extends JpaRepository<Animateur,Integer> {
    Optional<Animateur> findByUserId(Integer userId);
    Animateur findByUser(User user);
    Optional<Animateur> findByUserEmail(String email);
}