package com.cms.order.controller;

import com.base.domain.config.JwtAuthenticationProvider;
import com.cms.order.application.CartApplication;
import com.cms.order.domain.product.AddProductCartForm;
import com.cms.order.domain.redis.Cart;
import com.cms.order.service.CartService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/cutomer/cart")
@RequiredArgsConstructor
public class CustomerCartController {

	private final CartApplication cartApplication;
	private final JwtAuthenticationProvider provider;

	@PostMapping
	public ResponseEntity<Cart> addCart(
		@RequestHeader(name = "X-AUTH-TOKEN") String token, @RequestBody AddProductCartForm form) {
		return ResponseEntity.ok(cartApplication.addCart(provider.getUserVo(token).getId(), form));
	}

	@GetMapping
	public ResponseEntity<Cart> checkCart(@RequestHeader(name = "X-AUTH-TOKEN") String token) {
		return ResponseEntity.ok(cartApplication.getCart(provider.getUserVo(token).getId()));
	}

}
