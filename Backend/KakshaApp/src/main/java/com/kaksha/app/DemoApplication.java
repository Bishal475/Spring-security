package com.kaksha.app;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.kaksha.app.entity.AppUser;
import com.kaksha.app.entity.Role;
import com.kaksha.app.service.AppUserService;

import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@SpringBootApplication(scanBasePackages = "com.kaksha.app")
@Configuration
@EnableSwagger2
public class DemoApplication {

	public static void main(String[] args) {
		SpringApplication.run(DemoApplication.class, args);
	}

	@Bean
	CommandLineRunner run(AppUserService service) {
		// TODO Auto-generated method stub]
		return args ->{

			AppUser us = service.saveUser(new AppUser("Admin","Admin",null,"admin@admin.com","9078461512","Demo123",null));
			AppUser john = service.saveUser(new AppUser("John","Doe",null,"john.doe@gmail.com","8822912212","john4456",null));

			service.saveRole(new Role("ADMIN"));
			service.saveRole(new Role("INST"));
			service.saveRole(new Role("STD"));

			service.addRoleToUser(us.getUserName(), "ADMIN");
			service.addRoleToUser(john.getUserName(), "STD");
		};

	}

	@Bean
	public Docket productApi() {
		return new Docket(DocumentationType.SWAGGER_2)
				.select().apis(RequestHandlerSelectors.basePackage("com.kaksha.app.controller"))
				.paths(PathSelectors.any())
				.build();
	}
}
