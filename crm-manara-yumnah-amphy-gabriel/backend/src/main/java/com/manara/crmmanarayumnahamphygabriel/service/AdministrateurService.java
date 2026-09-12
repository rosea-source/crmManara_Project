package com.manara.crmmanarayumnahamphygabriel.service;


import com.manara.crmmanarayumnahamphygabriel.model.User;

import com.manara.crmmanarayumnahamphygabriel.repository.UserRepository; // Import mis à jour
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AdministrateurService {



    @Autowired
    private UserRepository userRepository; // Nom mis à jour



    // --- CRUD pour les utilisateurs ---
    public User ajouterUser(User u) { // Renommé pour éviter la confusion avec ajouterAdmin
        return userRepository.save(u);
    }

    public List<User> ListeUtilisateur() {
        return userRepository.findAll();
    }

    public User getUtilisateurbyID(Integer id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Impossible de trouver l'utilisateur avec l'id : " + id));
    }

    public void supprimerunUtilisateur(Integer id) {
        if (!userRepository.existsById(id)) {
            throw new IllegalArgumentException("Impossible de trouver l'utilisateur avec l'id : " + id);
        }
        userRepository.deleteById(id); // Correction : on utilise userRepository ici !
    }

    public User modifierUtilisateur(Integer idUtilisateur, User userDetails) {
        User user = getUtilisateurbyID(idUtilisateur);

        // Mise à jour des champs (Exemple)
        user.setNom(userDetails.getNom());
        user.setEmail(userDetails.getEmail());
        // Ajoutez ici les autres setters nécessaires (Prénom, etc.)

        return userRepository.save(user);
    }
}