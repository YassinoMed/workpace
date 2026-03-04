package iset.master.spring.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import iset.master.spring.model.Categorie;

public interface CategorieRepository extends JpaRepository<Categorie, Long> {
}