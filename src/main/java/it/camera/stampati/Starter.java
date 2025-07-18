package it.camera.stampati;

import org.burningwave.core.assembler.StaticComponentContainer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.ApplicationPidFileWriter;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import it.camera.stampati.repository.CustomRepositoryImpl;

@SpringBootApplication
// We need this declaration otherwise, Spring doesn't see the the method clear();
@EnableJpaRepositories(basePackages = "it.camera.stampati.repository", repositoryBaseClass = CustomRepositoryImpl.class)
public class Starter {

	@Value("${origins.allowed}")
	private String origins;

	public static void main(String[] args) {
		StaticComponentContainer.Modules.exportAllToAll();
		var app = new SpringApplication(Starter.class);
		app.addListeners(new ApplicationPidFileWriter("stampati.pid"));
		app.run(args);
	}

	@Bean
	public WebMvcConfigurer corsConfigurer() {
		return new WebMvcConfigurer() {

			@Override
			public void addCorsMappings(CorsRegistry registry) {
				registry.addMapping("/**")
				.allowedMethods("*")
				.allowedHeaders("*")
				.allowCredentials(true)
				.exposedHeaders("Authorization")
				.allowedOrigins(origins.split(","));
				//.allowedOriginPatterns("*"); 
			}
		};
	}
}
