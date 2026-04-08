package org.ms.factureservice;

import org.ms.factureservice.entities.Facture;
import org.ms.factureservice.entities.FactureLigne;
import org.ms.factureservice.feign.ClientServiceClient;
import org.ms.factureservice.feign.ProduitServiceClient;
import org.ms.factureservice.model.Client;
import org.ms.factureservice.model.Produit;
import org.ms.factureservice.repository.FactureLigneRepository;
import org.ms.factureservice.repository.FactureRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.hateoas.PagedModel;

import java.util.Date;
import java.util.Random;

@SpringBootApplication
@EnableFeignClients
public class FactureServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(FactureServiceApplication.class, args);
    }

    @Bean
    CommandLineRunner start(FactureRepository factureRepository,
                            FactureLigneRepository factureLigneRepository,
                            ClientServiceClient clientServiceClient,
                            ProduitServiceClient produitServiceClient) {
        return args -> {
            // Récupérer un client à distance
            Client client = clientServiceClient.findClientById(1L);
            if (client != null) {
                // Insérer une facture
                Facture facture = factureRepository.save(
                        new Facture(null, new Date(), null, client, client.getId())
                );

                // Récupérer les produits à distance
                PagedModel<Produit> listeProduits = produitServiceClient.getAllProduits();

                // Parcourir la liste des produits
                listeProduits.forEach(p -> {
                    // Pour chaque produit, insérer une factureligne
                    FactureLigne factureLigne = new FactureLigne();
                    factureLigne.setProduitID(p.getId());
                    factureLigne.setPrice(p.getPrice());
                    factureLigne.setQuantity(1 + new Random().nextInt(100));
                    factureLigne.setFacture(facture);
                    factureLigneRepository.save(factureLigne);
                });
            }
        };
    }
}
