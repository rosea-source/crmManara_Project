package com.manara.crmmanarayumnahamphygabriel.repository;

import com.manara.crmmanarayumnahamphygabriel.model.Parent;
import com.manara.crmmanarayumnahamphygabriel.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
@Repository
public interface ParentRepository extends JpaRepository<Parent, Integer> {

    Parent findByUser(User user);
    Parent findByUserEmail(String email);

}
