package com.kaksha.app.filter;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kaksha.app.utility.CommonFilterUtils;

@Component
public class CustomAuthenticationFilter extends UsernamePasswordAuthenticationFilter{

	@Autowired
	private AuthenticationManager authenticationManager;

	@Autowired
	private CommonFilterUtils commonFilterUtils;

	@Autowired
	Logger log; // = LoggerFactory.getLogger(CustomAuthenticationFilter.class);

//	public CustomAuthenticationFilter(AuthenticationManager authenticationManager) {
//		super();
//		this.authenticationManager = authenticationManager;
//	}


	@Override
	@Autowired
	public void setAuthenticationManager(AuthenticationManager authenticationManager) {
	    super.setAuthenticationManager(authenticationManager);
	}

	@Override
	public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
			throws AuthenticationException {
		String username = request.getParameter("username");
		String password = request.getParameter("password");
		log.info("Authentication Attempted by {} ",username);
		UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(username, password);
		return authenticationManager.authenticate(authToken);
	}

	@Override
	protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain,
			Authentication authResult) throws IOException, ServletException {
		User user = (User)authResult.getPrincipal();
		String accessToken = commonFilterUtils.generateAccessToken(user.getUsername(), request.getRequestURL().toString()
				, user.getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(Collectors.toList()));
		String refreshToken = commonFilterUtils.generateRefreshToken(user.getUsername(), request.getRequestURL().toString());

//		response.setHeader("access_token", accessToken);
//		response.setHeader("refresh_token", refreshToken);

		Map<String,String> tokens = new HashMap<>();
		tokens.put("access_token", accessToken);
		tokens.put("refresh_token", refreshToken);
		response.setContentType(MediaType.APPLICATION_JSON_VALUE);
		new ObjectMapper().writeValue(response.getOutputStream(), tokens);
	}

}
