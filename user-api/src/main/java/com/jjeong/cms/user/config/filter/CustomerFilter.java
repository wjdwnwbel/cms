package com.jjeong.cms.user.config.filter;

import com.base.domain.config.JwtAuthenticationProvider;
import com.base.domain.domain.common.UserVo;
import com.jjeong.cms.user.service.CustomerService;
import java.io.IOException;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

@WebFilter(urlPatterns = "/customer/*")
@RequiredArgsConstructor
public class CustomerFilter implements Filter {

	private final JwtAuthenticationProvider jwtAuthenticationProvider;
	private final CustomerService customerService;

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
		throws IOException, ServletException {
		HttpServletRequest req = (HttpServletRequest) request;
		String token = req.getHeader("X-AUTH-TOKEN");
		if(!jwtAuthenticationProvider.validateToken(token)) {
			throw new ServletException("Invalid Access");
		}

		UserVo vo = jwtAuthenticationProvider.getUserVo(token);
		customerService.findByIdAndEmail(vo.getId(), vo.getEmail()).orElseThrow(() -> new ServletException("Invalid access"));

		chain.doFilter(request, response);
	}
}


