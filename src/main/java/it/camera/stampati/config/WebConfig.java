package it.camera.stampati.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

//@Configuration
//@EnableWebMvc
//@PropertySource(value = {"classpath:application.properties"})
//@ComponentScan(basePackages= { "it.camera.stampati.controller", "it.camera.stampati.config" }) 
public class WebConfig implements WebMvcConfigurer {

//	@Autowired
//	private Environment env;
//	@Value("${origins.allowed}")
//	private String origins;
//
//	@Autowired
//	@Qualifier("xmlObjectMapper")
//	private ObjectMapper xmlMapper;
//
//	@Autowired
//	@Qualifier("jsonObjectMapper")
//	private ObjectMapper jsonMapper;

//	@Override
//	public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
//		converters.add(new MappingJackson2HttpMessageConverter());
//		converters.add(new MappingJackson2XmlHttpMessageConverter(xmlMapper));
//		WebMvcConfigurer.super.extendMessageConverters(converters);
//	}
//
//	@Override
//	public void configureContentNegotiation(ContentNegotiationConfigurer configurer) {
//		configurer.defaultContentType(MediaType.APPLICATION_JSON).favorPathExtension(true).ignoreAcceptHeader(true).useRegisteredExtensionsOnly(true).mediaType(MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_XML).mediaType(MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_JSON);
//		WebMvcConfigurer.super.configureContentNegotiation(configurer);
//	}

//	@Override
//	public void addCorsMappings(CorsRegistry registry) {
//		registry.addMapping("/**")
//		.allowedMethods("*")
//		.allowedHeaders("*")
//		.allowCredentials(true)
//		.exposedHeaders("Authorization")
//		.allowedOrigins(origins.split(","));
//	}
	
//	@Override
//	public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
//		resolvers.add(new PageableHandlerMethodArgumentResolver());
//	}
	
}