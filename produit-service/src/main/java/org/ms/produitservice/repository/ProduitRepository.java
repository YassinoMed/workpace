package org.ms.produitservice.repository;

import org.ms.produitservice.entities.Produit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource(path = "produits")
public interface ProduitRepository extends JpaRepository<Produit, Long> {
}
