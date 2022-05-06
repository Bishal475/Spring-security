package com.kaksha.app.filter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.auth0.jwt.JWT;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.auth0.jwt.interfaces.JWTVerifier;
import com.kaksha.app.utility.CommonFilterUtils;

@Component
public class CustomAuthorizationFilter extends OncePerRequestFilter{

	@Autowired
	private CommonFilterUtils filterUtils;

	@Autowired
	private Logger log;

	@Override
	protected void doFilterInternal(HttpServletRequest req, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		if(filterUtils.getPathEqual(req,new String[]{"/login","/api/user/save","/swagger-ui.html","/api/token/refresh"})) {
			filterChain.doFilter(req, response);
		}else {
			String authHeader = req.getHeader(HttpHeaders.AUTHORIZATION);
			if(authHeader !=null && authHeader.startsWith("Bearer ")) {
				try {
					String token = authHeader.substring("Bearer ".length());
					JWTVerifier verifier = JWT.require(filterUtils.getAlgo()).build();
					DecodedJWT decodedJWT = verifier.verify(token);
					String userName = decodedJWT.getSubject();
					String[] roles = decodedJWT.getClaim("roles").asArray(String.class);
					List<SimpleGrantedAuthority> authorities = new ArrayList<>();
					Arrays.stream(roles).forEach(role -> {
						authorities.add(new SimpleGrantedAuthority(role));
					});
					UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(userName, null, authorities);
					SecurityContextHolder.getContext().setAuthentication(authToken);
					filterChain.doFilter(req, response);
					log.info("User authorized with username {}",userName);
				}catch(Exception e) {
					log.error("Error Occured : {}",e.getMessage());
					response.setHeader("error",e.getMessage());
					response.sendError(HttpStatus.FORBIDDEN.value());
				}
			}else {
				filterChain.doFilter(req, response);
			}
		}
	}

}
