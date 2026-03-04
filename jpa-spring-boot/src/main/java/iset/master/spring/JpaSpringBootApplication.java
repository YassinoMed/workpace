package iset.master.spring;

import java.time.LocalDate;
import java.util.ArrayList;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.CommandLineRunner;
import org.springframework.transaction.annotation.Transactional;

import iset.master.spring.model.Categorie;
import iset.master.spring.model.Produit;
import iset.master.spring.model.Responsable;
import iset.master.spring.model.Stock;
import iset.master.spring.repository.CategorieRepository;
import iset.master.spring.repository.ProduitRepository;
import iset.master.spring.repository.ResponsableRepository;
import iset.master.spring.repository.StockRepository;

@SpringBootApplication
public class JpaSpringBootApplication implements CommandLineRunner {

    private final CategorieRepository categorieRepo;
    private final ProduitRepository produitRepo;
    private final StockRepository stockRepo;
    private final ResponsableRepository responsableRepo;

    public JpaSpringBootApplication(
            CategorieRepository categorieRepo,
            ProduitRepository produitRepo,
            StockRepository stockRepo,
            ResponsableRepository responsableRepo
    ) {
        this.categorieRepo = categorieRepo;
        this.produitRepo = produitRepo;
        this.stockRepo = stockRepo;
        this.responsableRepo = responsableRepo;
    }

    public static void main(String[] args) {
        SpringApplication.run(JpaSpringBootApplication.class, args);
    }

    @Override
    @Transactional
    public void run(String... args) {
        LocalDate date1 = LocalDate.parse("2026-01-01");
        LocalDate date2 = LocalDate.parse("2026-02-01");

        System.out.println("--- CREATE : Insertion avec cascades ---");

        Categorie catAlim = new Categorie("AL", "Alimentaire");
        Categorie catElec = new Categorie("EL", "Electronique");
        categorieRepo.save(catAlim);
        categorieRepo.save(catElec);

        Produit pYaourt = new Produit("Yaourt", 0.5, 100, date1, catAlim);
        Produit pChoco = new Produit("Chocolat", 2.0, 50, date2, catAlim);
        Produit pPhone = new Produit("Téléphone", 500.0, 10, date1, catElec);
        produitRepo.save(pYaourt);
        produitRepo.save(pChoco);
        produitRepo.save(pPhone);

        Stock sTunis = new Stock("1", "Tunis");
        Stock sSfax = new Stock("2", "Sfax");
        stockRepo.save(sTunis);
        stockRepo.save(sSfax);

        lierProduitStock(pYaourt, sTunis);
        lierProduitStock(pYaourt, sSfax);
        lierProduitStock(pChoco, sTunis);
        produitRepo.save(pYaourt);
        produitRepo.save(pChoco);

        Responsable rAli = new Responsable("Ben Saleh", "Ali");
        Responsable rOmar = new Responsable("Ben Ahmed", "Omar");
        lierStockResponsable(sTunis, rAli);
        lierStockResponsable(sSfax, rOmar);
        stockRepo.save(sTunis);
        stockRepo.save(sSfax);

        afficherTout();

        System.out.println("--- UPDATE : Modifications ---");

        catAlim.setLibelle("Alimentaire Bio");
        categorieRepo.save(catAlim);

        pYaourt.setCategorie(catElec);
        pYaourt.setPrix(0.6);
        produitRepo.save(pYaourt);

        sTunis.setAdresse("Tunis Centre");
        delierStockResponsable(sTunis);
        delierStockResponsable(sSfax);
        lierStockResponsable(sTunis, rOmar);
        stockRepo.save(sSfax);
        stockRepo.save(sTunis);

        rAli.setNom("Ben Saleh Modifié");
        responsableRepo.save(rAli);

        afficherTout();

        System.out.println("--- DELETE : Suppressions avec nettoyage ---");

        supprimerProduitEnNettoyantRelations(pPhone);
        supprimerStockEnNettoyantRelations(sSfax);

        supprimerCategorieEnNettoyantRelations(catElec);

        responsableRepo.delete(rAli);

        afficherTout();
    }

    private void lierProduitStock(Produit produit, Stock stock) {
        produit.getStocks().add(stock);
        stock.getProduits().add(produit);
    }

    private void delierProduitStock(Produit produit, Stock stock) {
        produit.getStocks().remove(stock);
        stock.getProduits().remove(produit);
    }

    private void lierStockResponsable(Stock stock, Responsable responsable) {
        stock.setResponsable(responsable);
        responsable.setStock(stock);
    }

    private void delierStockResponsable(Stock stock) {
        Responsable responsable = stock.getResponsable();
        if (responsable == null) {
            return;
        }
        stock.setResponsable(null);
        if (responsable.getStock() == stock) {
            responsable.setStock(null);
        }
        stockRepo.saveAndFlush(stock);
    }

    private void supprimerProduitEnNettoyantRelations(Produit produit) {
        for (Stock stock : new ArrayList<>(produit.getStocks())) {
            delierProduitStock(produit, stock);
        }
        produitRepo.saveAndFlush(produit);
        produitRepo.delete(produit);
        produitRepo.flush();
    }

    private void supprimerStockEnNettoyantRelations(Stock stock) {
        delierStockResponsable(stock);
        for (Produit produit : new ArrayList<>(stock.getProduits())) {
            delierProduitStock(produit, stock);
            produitRepo.save(produit);
        }
        stockRepo.saveAndFlush(stock);
        stockRepo.delete(stock);
        stockRepo.flush();
    }

    private void supprimerCategorieEnNettoyantRelations(Categorie categorie) {
        Long categorieId = categorie.getId();
        if (categorieId == null) {
            return;
        }
        for (Produit produit : new ArrayList<>(produitRepo.findAll())) {
            if (produit.getCategorie() != null && categorieId.equals(produit.getCategorie().getId())) {
                supprimerProduitEnNettoyantRelations(produit);
            }
        }
        categorieRepo.delete(categorie);
        categorieRepo.flush();
    }

    private void afficherTout() {
        System.out.println("--- READ : Affichage actuel ---");
        System.out.println("Catégories:");
        categorieRepo.findAll().forEach(System.out::println);

        System.out.println("Produits:");
        produitRepo.findAll().forEach(p -> {
            System.out.println(p);
            System.out.println("  Stocks: " + p.getStocks());
        });

        System.out.println("Stocks:");
        stockRepo.findAll().forEach(s -> {
            System.out.println(s);
            System.out.println("  Produits: " + s.getProduits());
            System.out.println("  Responsable: " + s.getResponsable());
        });

        System.out.println("Responsables:");
        responsableRepo.findAll().forEach(System.out::println);
        System.out.println("-------------------------------");
    }
}
