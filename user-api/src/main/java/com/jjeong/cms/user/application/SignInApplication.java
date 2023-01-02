package com.jjeong.cms.user.application;

import com.base.domain.config.JwtAuthenticationProvider;
import com.base.domain.domain.common.UserType;
import com.jjeong.cms.user.domain.SignInForm;
import com.jjeong.cms.user.domain.model.Customer;
import com.jjeong.cms.user.exception.CustomException;
import com.jjeong.cms.user.exception.ErrorCode;
import com.jjeong.cms.user.service.CustomerService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SignInApplication {
	private final CustomerService customerService;
	private final JwtAuthenticationProvider provider;


	public String customerLoginToken(SignInForm form) {
		// 1. 로그인 가능 여부
		Customer customer = customerService.findValidCustomer(form.getEmail(), form.getPassword())
			.orElseThrow(() -> new CustomException(ErrorCode.LOGIN_CHECK_FAIL));
		// 2. 토큰 발행

		// 3. 토큰 response
		return provider.createToken(customer.getEmail(), customer.getId(), UserType.CUSTOMER);
	}
}
