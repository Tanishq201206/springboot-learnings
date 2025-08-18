package com.polp.polp;

import com.polp.polp.Enm.Role;
import com.polp.polp.Model.User;
import com.polp.polp.Repo.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;

@SpringBootApplication
public class PolpApplication {

	public static void main(String[] args) {
		SpringApplication.run(PolpApplication.class, args);
	}

	@Bean
	public CommandLineRunner init(UserRepository userRepository, PasswordEncoder encoder) {
		return args -> {
			if (userRepository.findByUsername("admin").isEmpty()) {
				User admin = new User();
				admin.setUsername("admin");
				admin.setPassword(encoder.encode("admin123"));
				admin.setRole(Role.ADMIN);
				userRepository.save(admin);
			}

			if (userRepository.findByUsername("employee1").isEmpty()) {
				User emp = new User();
				emp.setUsername("employee1");
				emp.setPassword(encoder.encode("pass123"));
				emp.setRole(Role.EMPLOYEE);
				userRepository.save(emp);
			}

			if (userRepository.findByUsername("manager1").isEmpty()) {
				User mgr = new User();
				mgr.setUsername("manager1");
				mgr.setPassword(encoder.encode("pass123"));
				mgr.setRole(Role.MANAGER);
				userRepository.save(mgr);
			}
		};
	}

}
