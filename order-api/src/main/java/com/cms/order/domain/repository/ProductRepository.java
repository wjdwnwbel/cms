package com.cms.order.domain.repository;

import com.cms.order.domain.model.Product;
import java.util.Optional;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.EntityGraph.EntityGraphType;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, Long> {

	@EntityGraph(attributePaths = {"productItems"}, type = EntityGraphType.LOAD)
	Optional<Product> findWithProductItemsById(Long id);

	@EntityGraph(attributePaths = {"productItems"}, type = EntityGraphType.LOAD)
	Optional<Product> findBySellerIdAndId(Long sellerId, Long id);
}
