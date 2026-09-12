package com.manara.crmmanarayumnahamphygabriel.model;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "animateurs")
public class Animateur {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    // Lien vers l'utilisateur correspondant
    @OneToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    private String diplome;
    private String specialite;

    @Column(name = "date_embauche")
    private LocalDate dateEmbauche;

    public Animateur() {}

    public Animateur(User user, String diplome, String specialite, LocalDate dateEmbauche) {
        this.user = user;
        this.diplome = diplome;
        this.specialite = specialite;
        this.dateEmbauche = dateEmbauche;
    }

    // ===== Getters & Setters =====
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public String getDiplome() { return diplome; }
    public void setDiplome(String diplome) { this.diplome = diplome; }

    public String getSpecialite() { return specialite; }
    public void setSpecialite(String specialite) { this.specialite = specialite; }

    public LocalDate getDateEmbauche() { return dateEmbauche; }
    public void setDateEmbauche(LocalDate dateEmbauche) { this.dateEmbauche = dateEmbauche; }
}