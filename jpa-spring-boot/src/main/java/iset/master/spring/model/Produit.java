package iset.master.spring.model;

import java.time.LocalDate;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import java.util.ArrayList;
import java.util.Collection;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;

@Entity
@JacksonXmlRootElement(localName = "Produit")
public class Produit {

    @Id
    @GeneratedValue
    private Long id;

    @Column(length = 50)
    private String designation;

    private double prix;
    private int quantite;

    @Column
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    @JsonDeserialize(using = LocalDateDeserializer.class)
    @JsonSerialize(using = LocalDateSerializer.class)
    private LocalDate dateAchat;

    @ManyToOne
    private Categorie categorie;

    @ManyToMany(cascade = CascadeType.PERSIST)
    private Collection<Stock> stocks = new ArrayList<>();

    public Produit() {}
    public Produit(String designation, double prix, int quantite) {
        this.designation = designation;
        this.prix = prix;
        this.quantite = quantite;
    }
    public Produit(String designation, double prix, int quantite, LocalDate dateAchat) {
        this(designation, prix, quantite);
        this.dateAchat = dateAchat;
    }
    public Produit(String designation, double prix, int quantite, LocalDate dateAchat, Categorie categorie) {
        this(designation, prix, quantite, dateAchat);
        this.categorie = categorie;
    }
    public Produit(Long id, String designation, double prix, int quantite) {
        this.id = id;
        this.designation = designation;
        this.prix = prix;
        this.quantite = quantite;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getDesignation() { return designation; }
    public void setDesignation(String designation) { this.designation = designation; }

    public double getPrix() { return prix; }
    public void setPrix(double prix) { this.prix = prix; }

    public int getQuantite() { return quantite; }
    public void setQuantite(int quantite) { this.quantite = quantite; }

    public LocalDate getDateAchat() { return dateAchat; }
    public void setDateAchat(LocalDate dateAchat) { this.dateAchat = dateAchat; }

    public Categorie getCategorie() { return categorie; }
    public void setCategorie(Categorie categorie) { this.categorie = categorie; }

    public Collection<Stock> getStocks() { return stocks; }
    public void setStocks(Collection<Stock> stocks) { this.stocks = stocks; }

    @Override
    public String toString() {
        return "Produit [id=" + id + ", designation=" + designation + ", prix=" + prix 
               + ", quantite=" + quantite + ", dateAchat=" + dateAchat 
               + ", categorie=" + categorie + "]";
    }
}
