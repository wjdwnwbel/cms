package com.jjeong.cms.user.client;

import com.jjeong.cms.user.client.mailgun.SendMailForm;
import feign.Response;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.cloud.openfeign.SpringQueryMap;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;

@FeignClient(name = "mailgun", url = "https://api.mailgun.net/v3/")
@Qualifier("mailgun")
public interface MailgunClient {

	@PostMapping("sandbox89c62952dd1f48808850ea020e1920b4.mailgun.org/messages")
	ResponseEntity<String> sendEmail(@SpringQueryMap SendMailForm form);

}
