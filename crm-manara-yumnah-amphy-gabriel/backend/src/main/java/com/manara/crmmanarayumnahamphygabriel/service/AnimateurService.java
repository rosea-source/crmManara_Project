package com.manara.crmmanarayumnahamphygabriel.service;

import com.manara.crmmanarayumnahamphygabriel.model.Animateur;
import com.manara.crmmanarayumnahamphygabriel.model.User;
import com.manara.crmmanarayumnahamphygabriel.repository.AnimateurRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AnimateurService {

    @Autowired
    private AnimateurRepository animateurRepository;

    public List<Animateur> getAll() {
        return animateurRepository.findAll();
    }


    public Animateur getByUser(User user) {
        return animateurRepository.findByUserId(user.getId())
                .orElse(null);
    }

    public Animateur save(Animateur animateur){
        return animateurRepository.save(animateur);
    }

    public Animateur getById(Integer id){
        return animateurRepository.findById(id).orElse(null);
    }

    public Animateur getByEmail(String email) {
        return animateurRepository.findByUserEmail(email).orElse(null);
    }
}