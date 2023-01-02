package com.jjeong.cms.user.service;

import com.jjeong.cms.user.domain.model.Customer;
import com.jjeong.cms.user.domain.repository.CustomerRepository;
import java.util.Optional;
import javax.swing.text.html.Option;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomerService {
	private final CustomerRepository customerRepository;

	public Optional<Customer> findByIdAndEmail(Long id, String email) {
		return customerRepository.findById(id).stream().filter(customer -> customer.getEmail().equals(email))
			.findFirst();
	}


	// 로그인하기 위해 password와 인증여부 확인
	public Optional<Customer> findValidCustomer(String email, String password) {
		return customerRepository.findByEmail(email).stream().filter(
			customer -> customer.getPassword().equals(password)&&customer.isVerify()
		).findFirst();
	}
}
