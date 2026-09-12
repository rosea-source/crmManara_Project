package com.manara.crmmanarayumnahamphygabriel.service;

import com.manara.crmmanarayumnahamphygabriel.model.Enfant;
import com.manara.crmmanarayumnahamphygabriel.repository.EnfantRepository;
import com.manara.crmmanarayumnahamphygabriel.repository.InscriptionRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EnfantService {

    private final EnfantRepository enfantRepository;
    private final InscriptionRepository inscriptionRepository;

    public EnfantService(EnfantRepository enfantRepository,
                         InscriptionRepository inscriptionRepository) {
        this.enfantRepository = enfantRepository;
        this.inscriptionRepository = inscriptionRepository;
    }

    // Récupérer enfants par parent
    public List<Enfant> getByParentId(Integer parentId) {
        return enfantRepository.findByParentId(parentId);
    }

    // Récupérer tous les enfants
    public List<Enfant> getAll() {
        return enfantRepository.findAll();
    }

    //  Récupérer enfant par ID (SAFE)
    public Enfant getById(Integer id) {
        return enfantRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Enfant introuvable avec id = " + id));
    }

    // Sauvegarder / update
    public Enfant save(Enfant enfant) {
        return enfantRepository.save(enfant);
    }

    //  Supprimer enfant
    public void delete(Integer id) {
        Enfant enfant = getById(id);

        // Optionnel mais recommandé : supprimer les inscriptions liées
        inscriptionRepository.deleteAll(
                inscriptionRepository.findByEnfant(enfant)
        );

        enfantRepository.delete(enfant);
    }
}