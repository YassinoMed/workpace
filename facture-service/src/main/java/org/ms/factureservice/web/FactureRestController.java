package org.ms.factureservice.web;

import org.ms.factureservice.entities.Facture;
import org.ms.factureservice.feign.ClientServiceClient;
import org.ms.factureservice.feign.ProduitServiceClient;
import org.ms.factureservice.repository.FactureLigneRepository;
import org.ms.factureservice.repository.FactureRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class FactureRestController {
    private FactureRepository factureRepository;
    private FactureLigneRepository factureLigneRepository;
    private ClientServiceClient clientServiceClient;
    private ProduitServiceClient produitServiceClient;

    public FactureRestController(FactureRepository factureRepository,
                                 FactureLigneRepository factureLigneRepository,
                                 ClientServiceClient clientServiceClient,
                                 ProduitServiceClient produitServiceClient) {
        this.factureRepository = factureRepository;
        this.factureLigneRepository = factureLigneRepository;
        this.clientServiceClient = clientServiceClient;
        this.produitServiceClient = produitServiceClient;
    }

    @GetMapping(path = "/full-facture/{id}")
    public Facture getFacture(@PathVariable Long id) {
        Facture facture = factureRepository.findById(id).get();
        facture.setClient(clientServiceClient.findClientById(facture.getClientID()));
        facture.getFactureLignes().forEach(fl -> {
            fl.setProduit(produitServiceClient.findProductById(fl.getProduitID()));
        });
        return facture;
    }
}
