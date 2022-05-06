package com.kaksha.app.controller;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.kaksha.app.entity.AppUser;
import com.kaksha.app.service.AppUserService;

@RestController
@RequestMapping("/api")
public class UserController {

	@Autowired
	AppUserService service;


	@GetMapping("/users")
	public ResponseEntity<List<AppUser>> getUsers(){
		return ResponseEntity.ok().body(service.getUsers());
	}

	@PostMapping("/user/save")
	public ResponseEntity<AppUser> saveUser(@RequestBody AppUser user) throws URISyntaxException{
		URI uri = new URI("localhost:8080/api/user/save");
		return ResponseEntity.created(uri).body(service.saveUser(user));
	}

	@GetMapping("/role/addtouser")
	public ResponseEntity<AppUser> addRolToUser(@RequestParam String userName,@RequestParam String roleName){
		service.addRoleToUser(userName, roleName);
		return ResponseEntity.ok().body(service.getUserByUsername(userName));
	}

	@GetMapping("/user/{userName}")
	public ResponseEntity<AppUser> getUserByUserName(@PathVariable String userName){
		return ResponseEntity.ok().body(service.getUserByUsername(userName));
	}

	@GetMapping("/token/refresh")
	public ResponseEntity<Map<String,String>> refreshToken(@RequestHeader("Authorization") String auth) {
		return ResponseEntity.ok().body(service.createAccessTokenFromRefresh(auth));
	}
}
