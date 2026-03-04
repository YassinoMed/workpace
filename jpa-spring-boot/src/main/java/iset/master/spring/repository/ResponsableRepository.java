package iset.master.spring.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import iset.master.spring.model.Responsable;

public interface ResponsableRepository extends JpaRepository<Responsable, Long> {
}