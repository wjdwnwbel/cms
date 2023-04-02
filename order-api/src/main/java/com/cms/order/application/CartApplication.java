package com.cms.order.application;

import com.cms.order.domain.model.Product;
import com.cms.order.domain.model.ProductItem;
import com.cms.order.domain.product.AddProductCartForm;
import com.cms.order.domain.redis.Cart;
import com.cms.order.exception.CustomException;
import com.cms.order.exception.ErrorCode;
import com.cms.order.service.CartService;
import com.cms.order.service.ProductSearchService;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CartApplication {
	private final ProductSearchService productSearchService;
	private final CartService cartService;

	public Cart addCart(Long customerId, AddProductCartForm form) {
		Product product = productSearchService.getByProductId(form.getId());
		if(product == null) {
			throw new CustomException(ErrorCode.NOT_FOUND_PRODUCT);
		}

		Cart cart = cartService.getCart(customerId);
		if(cart != null && !addAble(cart, product, form)) {
			throw new CustomException(ErrorCode.ITEM_COUNT_NOT_ENOUGH);
		}

		return cartService.addCart(customerId, form);
	}

	// 기존의 장바구니와 추가하려는 장바구니 비교
	private boolean addAble(Cart cart, Product product,AddProductCartForm form) {
		Cart.Product cartProduct = cart.getProducts().stream().filter(p -> p.getId().equals(form.getId()))
			.findFirst().orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_PRODUCT));

		// 장바구니 안의 상품 수량 검사
		// 검색 속도를 위해 Map으로
		Map<Long, Integer> cartItemCountMap = cartProduct.getItems().stream()
			.collect(Collectors.toMap(Cart.ProductItem::getId, Cart.ProductItem::getCount));
		Map<Long, Integer> currentItemCountMap = product.getProductItems().stream()
			.collect(Collectors.toMap(ProductItem::getId, ProductItem::getCount));

		return form.getItems().stream().noneMatch(
			formItem -> {
				Integer cartCount = cartItemCountMap.get(formItem.getId());
				Integer currentCount = currentItemCountMap.get(formItem.getId());

				return formItem.getCount()+ cartCount > currentCount;
			});
	}
}
