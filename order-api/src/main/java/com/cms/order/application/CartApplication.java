package com.cms.order.application;

import com.cms.order.domain.model.Product;
import com.cms.order.domain.model.ProductItem;
import com.cms.order.domain.product.AddProductCartForm;
import com.cms.order.domain.redis.Cart;
import com.cms.order.exception.CustomException;
import com.cms.order.exception.ErrorCode;
import com.cms.order.service.CartService;
import com.cms.order.service.ProductSearchService;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
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

		if(cart != null && addAble(cart, product, form)) {
			throw new CustomException(ErrorCode.ITEM_COUNT_NOT_ENOUGH);
		}

		return cartService.addCart(customerId, form);
	}

	// 기존의 장바구니와 추가하려는 장바구니 비교
	private boolean addAble(Cart cart, Product product,AddProductCartForm form) {

		Cart.Product cartProduct = cart.getProducts().stream().filter(p -> p.getId().equals(form.getId()))
			.findFirst().orElse(Cart.Product.builder().id(product.getId())
				.items(Collections.emptyList()).build());

		// 장바구니 안의 상품 수량 검사
		// 검색 속도를 위해 Map으로
		Map<Long, Integer> cartItemCountMap = cartProduct.getItems().stream()
			.collect(Collectors.toMap(Cart.ProductItem::getId, Cart.ProductItem::getCount));
		Map<Long, Integer> currentItemCountMap = product.getProductItems().stream()
			.collect(Collectors.toMap(ProductItem::getId, ProductItem::getCount));

		return form.getItems().stream().noneMatch(
			formItem -> {
				Integer cartCount = cartItemCountMap.get(formItem.getId());
				if(cartCount == null) {
					cartCount = 0;
				}
				Integer currentCount = currentItemCountMap.get(formItem.getId());

				return formItem.getCount()+ cartCount > currentCount;
			});
	}

	// 1. 장바구니에 상품 추가
	// 2. 상품의 가격이나 수량이 변동되면 업데이트 되어야함
	public Cart getCart(Long customerId) {
		Cart cart = updateCart(cartService.getCart(customerId));
		Cart returnCart = cart.clone();

		cart.setMessages(new ArrayList<>());
		cartService.putCart(customerId, cart);

		return returnCart;

		//메세지 제거
	}

	private Cart updateCart(Cart cart) {
		// 변동사항 확인해야하므로 장바구니랑 물건 가져옴

		Map<Long, Product> productMap = productSearchService.getListByProductIds(
			cart.getProducts().stream().map(Cart.Product::getId).collect(Collectors.toList()))
				.stream().collect(Collectors.toMap(Product::getId, product -> product));

		// 비교, 알림
		for (int i = 0; i < cart.getProducts().size(); i++) {		// 상품 목록들 가져와서
			Cart.Product cartProduct = cart.getProducts().get(i);
			Product p = productMap.get(cartProduct.getId());
			if(p == null) {
				cart.getProducts().remove(cartProduct);
				i--;
				cart.addMessage(cartProduct.getName()+" 상품이 삭제되었습니다.");
				continue;
			}

			Map<Long, ProductItem> productItemMap = p.getProductItems().stream()
				.collect(Collectors.toMap(ProductItem::getId, productItem -> productItem));

			List<String> tmpMessages = new ArrayList<>();
			for(int j = 0; j < cartProduct.getItems().size(); j++) {
				Cart.ProductItem productItem = cartProduct.getItems().get(j);
				ProductItem pi = productItemMap.get(cartProduct.getId());
				if(pi == null) {
					cartProduct.getItems().remove(productItem);
					j--;
					tmpMessages.add(productItem.getName()+" 옵션이 삭제되었습니다.");
					continue;
				}

				boolean isPriceChanged = false, isCountNotEnough=false;

				if(productItem.getPrice().equals(pi.getPrice())) {
					isPriceChanged = true;
					productItem.setPrice(pi.getPrice());
				}
				if(productItem.getCount() > pi.getCount()) {
					isCountNotEnough = true;
					productItem.setCount(pi.getCount());
				}
				if(isPriceChanged && isCountNotEnough) {
					tmpMessages.add(productItem.getName()+" 가격변동 혹은 수량이 부족하여 구매 가능한 최대치로 변동되었습니다.");
				} else if (isPriceChanged) {
					tmpMessages.add(productItem.getName()+" 가격이 변동되었습니다.");
				} else if (isCountNotEnough) {
					tmpMessages.add(productItem.getName()+" 수량이 부족하여 구매 가능한 최대치로 변동되었습니다.");
				}
			}

			// 상품의 옵션들이 없어진 경우, 장바구니에서 삭제
			if(cartProduct.getItems().size() == 0) {
				cart.getProducts().remove(cartProduct);
				i--;
				cart.addMessage(cartProduct.getName()+ " 상품의 옵션이 모두 없어져 구매가 불가능합니다.");
			}
			// 문제 있음
			else if(tmpMessages.size() > 0) {
				StringBuilder builder = new StringBuilder();
				builder.append(cartProduct.getName()+" 상품의 변동 사항 : ");
				for(String message : tmpMessages) {
					builder.append(message);
					builder.append(", ");
				}
				cart.addMessage(builder.toString());
			}
		}
		cartService.putCart(cart.getCustomerId(), cart);
		return cart;
	}

	public void clearCart(Long customerId) {
		cartService.putCart(customerId, null);
	}
}
