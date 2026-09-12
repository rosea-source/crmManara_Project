package com.manara.crmmanarayumnahamphygabriel.service;

import com.manara.crmmanarayumnahamphygabriel.model.Animateur;
import com.manara.crmmanarayumnahamphygabriel.model.Parent;
import com.manara.crmmanarayumnahamphygabriel.model.User;
import com.manara.crmmanarayumnahamphygabriel.repository.AnimateurRepository;
import com.manara.crmmanarayumnahamphygabriel.repository.ParentRepository;
import com.manara.crmmanarayumnahamphygabriel.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final ParentRepository parentRepository;
    private final AnimateurRepository animateurRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository,
                       ParentRepository parentRepository,
                       AnimateurRepository animateurRepository,
                       PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.parentRepository = parentRepository;
        this.animateurRepository = animateurRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public void register(User user) {
        if (userRepository.findByEmail(user.getEmail()).isPresent()) {
            throw new IllegalArgumentException("Cet email est déjà utilisé");
        }

        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setRole("ROLE_PARENT");

        userRepository.save(user);

        Parent parent = new Parent();
        parent.setUser(user);
        parentRepository.save(parent);
    }

    public void createUser(User user) {
        if (userRepository.findByEmail(user.getEmail()).isPresent()) {
            throw new IllegalArgumentException("Cet email est déjà utilisé");
        }

        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userRepository.save(user);

        if ("ROLE_PARENT".equals(user.getRole())) {
            Parent parent = new Parent();
            parent.setUser(user);
            parentRepository.save(parent);
        }

        if ("ROLE_ANIMATEUR".equals(user.getRole())) {
            Animateur animateur = new Animateur();
            animateur.setUser(user);
            animateurRepository.save(animateur);
        }
    }

    public List<User> getAll() {
        return userRepository.findAll();
    }

    public Optional<User> findUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public User getById(Integer id) {
        return userRepository.findById(id).orElse(null);
    }

    public void delete(Integer id) {
        User user = userRepository.findById(id).orElse(null);

        if (user != null && !"ROLE_ADMIN".equals(user.getRole())) {
            userRepository.deleteById(id);
        }
    }
    public User findByEmail(String email) {
        return userRepository.findByEmail(email).orElse(null);
    }

    public User save(User user) {
        return userRepository.save(user);
    }

    public List<User> getMessageReceivers() {
        return userRepository.findByRoleIn(List.of("ROLE_PARENT", "ROLE_ANIMATEUR"));
    }
}