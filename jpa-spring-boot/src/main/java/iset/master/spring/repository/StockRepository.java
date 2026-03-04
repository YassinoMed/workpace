package iset.master.spring.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import iset.master.spring.model.Stock;

public interface StockRepository extends JpaRepository<Stock, Long> {
}