package com.kaksha.app.utility;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;

@Component
public class CommonFilterUtils {

	@Value("${kaksha.sceret.key}")
	String secret;

	Algorithm algo;

	@PostConstruct
    public void init() {
        algo = Algorithm.HMAC256(secret.getBytes());
    }



	public String generateAccessToken(String username, String issuer, List claim) {

		
		
		return JWT.create()
				.withSubject(username).withExpiresAt(new Date(System.currentTimeMillis() + 10*60*1000))
				.withIssuer(issuer).withClaim("roles", claim )
				.sign(algo);

	}

	public String generateRefreshToken(String username, String issuer) {

		return JWT.create()
				.withSubject(username).withExpiresAt(new Date(System.currentTimeMillis() + 30*60*1000))
				.sign(algo);

	}
	
	public boolean getPathEqual(HttpServletRequest request,String[] urls) {
		boolean flag = false;
		for(String url : urls) {
			if(request.getServletPath().equals(url)) {
				flag = true;
			}
		}
		return flag;
	}

	public Algorithm getAlgo() {
		return algo;
	}
}
