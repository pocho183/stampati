package it.camera.stampati.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import it.esinware.mapping.BeanMapper;

@Configuration
public class Application {

	@Bean
	public BeanMapper initBeanMapper() {
		return new BeanMapper();
	}
}