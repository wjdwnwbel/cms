package com.jjeong.cms.user.application;

import com.jjeong.cms.user.client.MailgunClient;
import com.jjeong.cms.user.client.mailgun.SendMailForm;
import com.jjeong.cms.user.domain.SignUpForm;
import com.jjeong.cms.user.domain.model.Customer;
import com.jjeong.cms.user.exception.CustomException;
import com.jjeong.cms.user.exception.ErrorCode;
import com.jjeong.cms.user.service.SignUpCustomerService;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class SignUpApplication {
	private final MailgunClient mailgunClient;
	private final SignUpCustomerService signUpCustomerService;

	public void customerVerify(String email, String code) {
		signUpCustomerService.verifyEmail(email, code);
	}

	public String customerSignUp(SignUpForm form) {
		if(signUpCustomerService.isEmailExist(form.getEmail())) {
			throw new CustomException(ErrorCode.ALREADY_REGISTER_USER);
		} else {
			Customer c = signUpCustomerService.signUp(form);
			LocalDateTime now = LocalDateTime.now();

			String code = getRandomCode();
			SendMailForm sendMailForm = SendMailForm.builder()
				.from("hjj0518@naver.com")
				.to("wjdwnwbel@gmail.com")
				.subject("Verification Email!")
				.text(getVerificationEmailBody(c.getEmail(), c.getName(), code))
				.build();

			log.info("Send email result : " + mailgunClient.sendEmail(sendMailForm).getBody());
			signUpCustomerService.ChangesCustomerValidateEmail(c.getId(), code);
			return "회원 가입에 성공하였습니다. ";
		}
	}
	private String getRandomCode() {
		return RandomStringUtils.random(10, true, true);
	}

	private String getVerificationEmailBody(String email, String name, String code) {
		StringBuilder builder = new StringBuilder();
		return builder.append("Hello ").append(name).append("! Please Click Link for verification\n\n")
			.append("http://localhost:8081/customer/verify/customer?email=")
			.append(email)
			.append("&code=")
			.append(code).toString();
	}
}
