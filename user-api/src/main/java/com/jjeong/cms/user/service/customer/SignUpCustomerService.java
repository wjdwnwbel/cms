package com.jjeong.cms.user.service.customer;

import com.jjeong.cms.user.domain.SignUpForm;
import com.jjeong.cms.user.domain.model.Customer;
import com.jjeong.cms.user.domain.repository.CustomerRepository;
import com.jjeong.cms.user.exception.CustomException;
import com.jjeong.cms.user.exception.ErrorCode;
import java.time.LocalDateTime;
import java.util.Locale;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class SignUpCustomerService {

	private final CustomerRepository customerRepository;

	public Customer signUp(SignUpForm form) {
		return customerRepository.save(Customer.from(form));
	}

	public boolean isEmailExist(String email) {
		return customerRepository.findByEmail(email.toLowerCase(Locale.ROOT))
			.isPresent();
	}

	// 이메일 인증
	@Transactional
	public void verifyEmail(String email, String code) {
		Customer customer = customerRepository.findByEmail(email).orElseThrow(()-> new CustomException(
			ErrorCode.NOT_FOUND_USER));
		if(customer.isVerify()) {
			throw new CustomException(ErrorCode.ALREADY_VERIFY);
		} else if(!customer.getVerificationCode().equals(code)) {
			throw new CustomException(ErrorCode.WRONG_VERIFICATION);
		} else if(customer.getVerifyExpiredAt().isBefore(LocalDateTime.now())) {
			throw new CustomException(ErrorCode.EXPIRE_CODE);
		}

		customer.setVerify(true);
	}

	// 인증이 완료된 고객 상태 변경
	@Transactional
	public LocalDateTime ChangesCustomerValidateEmail(Long customerId, String verificationCode) {
		Optional<Customer> customerOptional = customerRepository.findById(customerId);

		if(customerOptional.isPresent()) {
			Customer customer = customerOptional.get();
			customer.setVerificationCode(verificationCode);
			customer.setVerifyExpiredAt(LocalDateTime.now().plusDays(1));

			return  customer.getVerifyExpiredAt();
		}
		throw new CustomException(ErrorCode.NOT_FOUND_USER);
	}
}
