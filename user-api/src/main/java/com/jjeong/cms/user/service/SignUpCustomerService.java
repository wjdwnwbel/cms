package com.jjeong.cms.user.service;

import com.jjeong.cms.user.domain.SignUpForm;
import com.jjeong.cms.user.domain.model.Customer;
import com.jjeong.cms.user.domain.repository.CustomerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SignUpCustomerService {

	private final CustomerRepository customerRepository;

	public Customer signUp(SignUpForm form) {
		return customerRepository.save(Customer.from(form));
	}
}
