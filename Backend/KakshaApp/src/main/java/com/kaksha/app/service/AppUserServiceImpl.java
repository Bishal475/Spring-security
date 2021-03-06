package com.kaksha.app.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.SequenceGenerator;

import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.auth0.jwt.JWT;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.auth0.jwt.interfaces.JWTVerifier;
import com.kaksha.app.entity.AppUser;
import com.kaksha.app.entity.Role;
import com.kaksha.app.repo.AppUserRepo;
import com.kaksha.app.repo.RoleRepo;
import com.kaksha.app.utility.CommonFilterUtils;

@Transactional
@Service
public class AppUserServiceImpl implements AppUserService, UserDetailsService {

	@Autowired
	private AppUserRepo userRepo;

	@Autowired
	private RoleRepo roleRepo;

	@Autowired
	Logger log;

	@Autowired
	private CommonFilterUtils filterUtils;

	@Autowired
	private BCryptPasswordEncoder passwordEncoder;

	@Autowired
	private Date date;

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

		AppUser user = getUserByUserNameOREmailORMobile(username);
		if(user==null) {
			log.error("User not found");
			throw new UsernameNotFoundException("User Not Found in the Database");
		}else {
			log.info("User Found. User = {}",user);
		}

		List<SimpleGrantedAuthority> authorities = new ArrayList<>();
		user.getRoles().forEach(role -> {
			authorities.add(new SimpleGrantedAuthority(role.getName()));
		});

		User u = new User(user.getUserName(),user.getPassword(),authorities);
		return u;
	}

	public AppUser getUserByUserNameOREmailORMobile(String query) {
		AppUser user = userRepo.findByUserName(query);
		if(user==null) {
			user = userRepo.findByEmail(query);
			if(user==null) {
				user= userRepo.findByMobile(query);
			}
		}
		return user;
	}

	@Override
	public AppUser saveUser(AppUser user) {
		log.info("Saving User {} ",user);
		user.setUserName(user.getFirstName()+user.getLastName().toUpperCase().charAt(0)+date.getDay()+date.getMonth()+date.getYear());
		user.setPassword(passwordEncoder.encode(user.getPassword()));
		return userRepo.save(user);
	}

	@Override
	public Role saveRole(Role role) {
		log.info("Saving Role {} ",role);
		return roleRepo.save(role);
	}

	@Override
	public AppUser getUserByUsername(String userName) {
		log.info("USer : ",userRepo.findByUserName(userName));
		return userRepo.findByUserName(userName);
	}

	@Override
	public AppUser getUserByEmail(String email) {
		return userRepo.findByEmail(email);
	}

	@Override
	public AppUser getUserByMobile(String mobile) {
		return userRepo.findByMobile(mobile);
	}

	@Override
	public List<AppUser> getUsers() {
		return userRepo.findAll();
	}

	@Override
	public void addRoleToUser(String userName, String roleName) {
		AppUser user = userRepo.findByUserName(userName);
		Role role = roleRepo.findByName(roleName);

		user.getRoles().add(role);
	}

	@Override
	public Map<String,String> createAccessTokenFromRefresh(String auth) {
		Map<String,String> res = new HashMap<>();
		if(auth != null && auth.startsWith("Bearer ")) {
			try {
				String token = auth.substring("Bearer ".length());
				JWTVerifier verifier = JWT.require(filterUtils.getAlgo()).build();
				DecodedJWT decodedJWT = verifier.verify(token);
				String userName = decodedJWT.getSubject();
				AppUser user = getUserByUserNameOREmailORMobile(userName);
				String accessToken = filterUtils.generateAccessToken(userName,"http://localhost:8080/token/refresh", user.getRoles());
				res.put("access_token", accessToken);
				res.put("refresh_token", auth);
				log.info("Access Token Generated from Refresh Token for User {}",userName);
			}catch(Exception e) {
				log.error("Error Occured : {}",e.getMessage());
				res.put("error", e.getMessage());
			}
		}else {
			throw new RuntimeException("Refersh Token Required.");
		}
		return res;
	}

}
