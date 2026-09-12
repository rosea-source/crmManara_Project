package com.manara.crmmanarayumnahamphygabriel;

import com.manara.crmmanarayumnahamphygabriel.model.User;
import com.manara.crmmanarayumnahamphygabriel.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.security.crypto.password.PasswordEncoder;

@SpringBootApplication
@EnableAsync
public class CrmManaraYumnahAmphyGabrielApplication {

    public static void main(String[] args) {
        SpringApplication.run(CrmManaraYumnahAmphyGabrielApplication.class, args);
    }

    @Bean
    CommandLineRunner initAdmin(UserRepository repo, PasswordEncoder encoder) {
        return args -> {

            if(repo.findByEmail("admin@manara.com").isEmpty()){

                User admin = new User();
                admin.setPrenom("Admin");
                admin.setNom("Manara");
                admin.setEmail("admin@manara.com");
                admin.setPassword(encoder.encode("admin123"));
                admin.setRole("ROLE_ADMIN");
                admin.setTelephone("8190000001");

                repo.save(admin);

                System.out.println("Admin créé : admin@manara.com / admin123");
            }
        };
    }
}