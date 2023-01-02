package com.jjeong.cms.user.controller;

import com.base.domain.config.JwtAuthenticationProvider;
import com.base.domain.domain.common.UserVo;
import com.jjeong.cms.user.domain.customer.CustomerDto;
import com.jjeong.cms.user.domain.model.Customer;
import com.jjeong.cms.user.exception.CustomException;
import com.jjeong.cms.user.exception.ErrorCode;
import com.jjeong.cms.user.service.customer.CustomerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/customer")
@RequiredArgsConstructor
public class CustomerController {

	private final JwtAuthenticationProvider provider;
	private final CustomerService customerService;

	@GetMapping("/getInfo")
	public ResponseEntity<CustomerDto> getInfo(@RequestHeader(name = "X-AUTH-TOKEN") String token) {
		UserVo vo = provider.getUserVo(token);
		Customer c = customerService.findByIdAndEmail(vo.getId(), vo.getEmail()).orElseThrow(() -> new CustomException(
			ErrorCode.NOT_FOUND_USER));

		return ResponseEntity.ok(CustomerDto.from(c));
	}

}
