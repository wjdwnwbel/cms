package com.cms.order.controller;

import com.base.domain.config.JwtAuthenticationProvider;
import com.cms.order.domain.product.ProductDto;
import com.cms.order.service.ProductSearchService;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/search/product")
@RequiredArgsConstructor
public class SearchController {
	private final ProductSearchService productSearchService;

	@GetMapping
	public ResponseEntity<List<ProductDto>> searchByName(@RequestParam String name) {
		return ResponseEntity.ok(
			productSearchService.searchByName(name).stream()
				.map(ProductDto::withoutItemsfrom).collect(Collectors.toList())
		);
	}

	@GetMapping("/detail")
	public ResponseEntity<ProductDto> getDetail(@RequestParam Long productId) {
		return ResponseEntity.ok(
			ProductDto.from(productSearchService.getByProductId(productId)));
	}
}
