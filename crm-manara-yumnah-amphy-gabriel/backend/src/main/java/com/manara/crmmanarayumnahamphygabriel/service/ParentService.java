package com.manara.crmmanarayumnahamphygabriel.service;

import com.manara.crmmanarayumnahamphygabriel.model.Parent;
import com.manara.crmmanarayumnahamphygabriel.model.User;
import com.manara.crmmanarayumnahamphygabriel.repository.ParentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ParentService {

    @Autowired
    private ParentRepository parentRepository;

    public Parent getByUser(User user){
        return parentRepository.findByUser(user);
    }

    // AJOUTER CETTE MÉTHODE
    public Parent save(Parent parent){
        return parentRepository.save(parent);
    }


}