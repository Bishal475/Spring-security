package com.kaksha.app.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.kaksha.app.entity.AppUser;

@Repository
public interface AppUserRepo extends JpaRepository<AppUser, Long> {

	AppUser findByUserName(String userName);
	AppUser findByEmail(String email);
	AppUser findByMobile(String mobile);
}
