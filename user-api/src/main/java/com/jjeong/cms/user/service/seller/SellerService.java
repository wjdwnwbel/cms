package com.jjeong.cms.user.service.seller;

import com.jjeong.cms.user.domain.SignUpForm;
import com.jjeong.cms.user.domain.model.Customer;
import com.jjeong.cms.user.domain.model.Seller;
import com.jjeong.cms.user.domain.repository.SellerRepository;
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
public class SellerService {
	private final SellerRepository sellerRepository;

	public Optional<Seller> findByIdAndEmail(Long id, String email) {
		return sellerRepository.findByIdAndEmail(id, email);
	}

	public Optional<Seller> findValidSeller(String email, String password) {
		return sellerRepository.findByEmailAndPasswordAndVerifyIsTrue(email, password);
	}


	// 회원 가입
	public Seller signUp(SignUpForm form) {
		return sellerRepository.save(Seller.from(form));
	}

	public boolean isEmailExist(String email) {
		return sellerRepository.findByEmail(email).isPresent();
	}

	// 이메일 인증
	@Transactional
	public void verifyEmail(String email, String code) {
		Seller seller = sellerRepository.findByEmail(email).orElseThrow(()-> new CustomException(
			ErrorCode.NOT_FOUND_USER));
		if(seller.isVerify()) {
			throw new CustomException(ErrorCode.ALREADY_VERIFY);
		} else if(!seller.getVerificationCode().equals(code)) {
			throw new CustomException(ErrorCode.WRONG_VERIFICATION);
		} else if(seller.getVerifyExpiredAt().isBefore(LocalDateTime.now())) {
			throw new CustomException(ErrorCode.EXPIRE_CODE);
		}

		seller.setVerify(true);
	}

	// 인증이 완료된 고객 상태 변경
	@Transactional
	public LocalDateTime ChangesSellerValidateEmail(Long id, String verificationCode) {
		Optional<Seller> sellerOptional = sellerRepository.findById(id);

		if(sellerOptional.isPresent()) {
			Seller seller = sellerOptional.get();
			seller.setVerificationCode(verificationCode);
			seller.setVerifyExpiredAt(LocalDateTime.now().plusDays(1));

			return  seller.getVerifyExpiredAt();
		}
		throw new CustomException(ErrorCode.NOT_FOUND_USER);
	}
}
