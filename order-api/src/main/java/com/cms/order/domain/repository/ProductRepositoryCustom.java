package com.cms.order.domain.repository;

import com.cms.order.domain.model.Product;
import java.util.List;

public interface ProductRepositoryCustom {
	List<Product> searchByName(String name);
}
