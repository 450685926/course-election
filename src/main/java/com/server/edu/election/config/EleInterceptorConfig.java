package com.server.edu.election.config;

import org.apache.ibatis.plugin.Interceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.server.edu.election.util.EleSqlStatementInterceptor;

@Configuration
public class EleInterceptorConfig {

	@Bean
	public Interceptor getInterceptor(){
		return new EleSqlStatementInterceptor();
	}

}
