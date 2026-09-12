package com.manara.crmmanarayumnahamphygabriel.repository;

import com.manara.crmmanarayumnahamphygabriel.model.Enfant;
import com.manara.crmmanarayumnahamphygabriel.model.Parent;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface EnfantRepository extends JpaRepository<Enfant,Integer> {

    List<Enfant> findByParent(Parent parent);

    List<Enfant> findByParentId(Integer parentId);
}