package iset.master.spring.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;

@Entity
public class Responsable {

    @Id
    @GeneratedValue
    private Long id;

    @Column(length = 50)
    private String nom;

    @Column(length = 50)
    private String prenom;

    @OneToOne(mappedBy = "responsable")
    private Stock stock;

    // Constructeurs
    public Responsable() {}
    public Responsable(String nom, String prenom) {
        this.nom = nom;
        this.prenom = prenom;
    }
    public Responsable(String nom, String prenom, Stock stock) {
        this.nom = nom;
        this.prenom = prenom;
        this.stock = stock;
    }

    // Getters & Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }

    public String getPrenom() { return prenom; }
    public void setPrenom(String prenom) { this.prenom = prenom; }

    @JsonIgnore
    public Stock getStock() { return stock; }
    public void setStock(Stock stock) { this.stock = stock; }

    @Override
    public String toString() {
        return "Responsable [id=" + id + ", nom=" + nom + ", prenom=" + prenom 
               + ", stock=" + stock + "]";
    }
}