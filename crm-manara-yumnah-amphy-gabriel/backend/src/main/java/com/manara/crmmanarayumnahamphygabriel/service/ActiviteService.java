package com.manara.crmmanarayumnahamphygabriel.service;

import com.manara.crmmanarayumnahamphygabriel.model.Activite;
import com.manara.crmmanarayumnahamphygabriel.repository.ActiviteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class ActiviteService {

    @Autowired
    private ActiviteRepository activiteRepository;

    public List<Activite> getAll() {
        return activiteRepository.findAll();
    }

    public Activite getById(Integer id) {
        return activiteRepository.findById(id).orElse(null);
    }

    public Activite save(Activite activite) {
        return activiteRepository.save(activite);
    }

    public void delete(Integer id) {
        activiteRepository.deleteById(id);
    }
}