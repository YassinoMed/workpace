package iset.master.spring.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import iset.master.spring.model.Produit;

public interface ProduitRepository extends JpaRepository<Produit, Long> {}