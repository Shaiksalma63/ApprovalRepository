package com.in.xoriant.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.in.xoriant.modelmapper.ModelMapper;

@Configuration
public class AppConfig {
	@Bean
	public ModelMapper getModelMapper() {
		return new ModelMapper();
	}
}
