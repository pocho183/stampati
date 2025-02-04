package it.camera.stampati.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.PropertySource;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.JsonGenerator.Feature;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.zaxxer.hikari.HikariDataSource;

import it.esinware.mapping.BeanMapper;

@Configuration
@PropertySource(value = {"classpath:/application.properties", "classpath:persistence.properties"})
public class Application {

	@Bean
	public BeanMapper initBeanMapper() {
		return new BeanMapper();
	}

	@Bean(value = "xmlObjectMapper")
	public ObjectMapper xmlObjectMapper() {
		XmlMapper mapper = new XmlMapper();
		return mapper.enable(SerializationFeature.INDENT_OUTPUT).enable(SerializationFeature.WRITE_ENUMS_USING_TO_STRING)
				.setDefaultPropertyInclusion(Include.NON_EMPTY).disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
				.enable(Feature.IGNORE_UNKNOWN);
	}

	@Bean(value = "jsonObjectMapper")
	@Primary
	public ObjectMapper jsonObjectMapper() {
		ObjectMapper mapper = new ObjectMapper();
		mapper.enable(SerializationFeature.INDENT_OUTPUT);
		mapper.enable(SerializationFeature.WRITE_ENUMS_USING_TO_STRING);
		mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
		mapper.enable(Feature.IGNORE_UNKNOWN);
		return mapper;
	}

	@Bean
    @ConfigurationProperties(prefix = "spring.datasource")
    public HikariDataSource dataSource() {
        return DataSourceBuilder.create().type(HikariDataSource.class).build();
    }
}