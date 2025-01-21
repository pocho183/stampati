package it.camera.stampati.config;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.http.converter.xml.MappingJackson2XmlHttpMessageConverter;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.ContentNegotiationConfigurer;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.fasterxml.jackson.databind.ObjectMapper;

@Configuration
@EnableWebMvc
@PropertySource(value = {"classpath:application.properties"})
@ComponentScan(basePackages= { "it.camera.stampati.controller", "it.camera.stampati.config" }) 
public class WebConfig implements WebMvcConfigurer {

	@Autowired
	private Environment env;

	@Autowired
	@Qualifier("xmlObjectMapper")
	private ObjectMapper xmlMapper;

	@Autowired
	@Qualifier("jsonObjectMapper")
	private ObjectMapper jsonMapper;

	@Override
	public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
		converters.add(new MappingJackson2HttpMessageConverter());
		converters.add(new MappingJackson2XmlHttpMessageConverter(xmlMapper));
		WebMvcConfigurer.super.extendMessageConverters(converters);
	}

	@Override
	public void configureContentNegotiation(ContentNegotiationConfigurer configurer) {
		configurer.defaultContentType(MediaType.APPLICATION_JSON).favorPathExtension(true).ignoreAcceptHeader(true).useRegisteredExtensionsOnly(true).mediaType(MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_XML).mediaType(MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_JSON);
		WebMvcConfigurer.super.configureContentNegotiation(configurer);
	}

	@Override
	public void addCorsMappings(CorsRegistry registry) {
		registry.addMapping("/**")
		.allowedMethods("*")
		.allowedHeaders("*")
		.allowCredentials(true)
		.exposedHeaders("Authorization")
		.allowedOrigins(env.getProperty("origins.allowed").split(","));
	}
	
	@Override
	public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
		resolvers.add(new PageableHandlerMethodArgumentResolver());
	}
	
}