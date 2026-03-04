package iset.master.spring.model;

import java.util.ArrayList;
import java.util.Collection;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToOne;

@Entity
public class Stock {

    @Id
    @GeneratedValue
    private Long id;

    @Column(length = 50)
    private String code;

    @Column(length = 50)
    private String adresse;

    @ManyToMany(mappedBy = "stocks")
    private Collection<Produit> produits = new ArrayList<>();

    @OneToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    private Responsable responsable;

    // Constructeurs
    public Stock() {}
    public Stock(String code, String adresse) {
        this.code = code;
        this.adresse = adresse;
    }
    public Stock(String code, String adresse, Responsable responsable) {
        this.code = code;
        this.adresse = adresse;
        this.responsable = responsable;
    }

    // Getters & Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }

    public String getAdresse() { return adresse; }
    public void setAdresse(String adresse) { this.adresse = adresse; }

    @JsonIgnore
    public Collection<Produit> getProduits() { return produits; }
    public void setProduits(Collection<Produit> produits) { this.produits = produits; }

    public Responsable getResponsable() { return responsable; }
    public void setResponsable(Responsable responsable) { this.responsable = responsable; }

    @Override
    public String toString() {
        return "Stock [id=" + id + ", code=" + code + ", adresse=" + adresse + "]";
    }
}
