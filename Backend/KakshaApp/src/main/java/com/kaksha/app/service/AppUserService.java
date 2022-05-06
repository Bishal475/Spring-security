package com.kaksha.app.service;

import java.util.List;
import java.util.Map;

import com.kaksha.app.entity.AppUser;
import com.kaksha.app.entity.Role;

public interface AppUserService {

	AppUser saveUser(AppUser user);
	Role saveRole(Role role);
	AppUser getUserByUsername(String userName);
	AppUser getUserByEmail(String email);
	AppUser getUserByMobile(String mobile);
	List<AppUser> getUsers();
	void addRoleToUser(String userName, String roleName);
	public Map<String,String> createAccessTokenFromRefresh(String auth);
}
