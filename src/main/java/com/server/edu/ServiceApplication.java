package com.server.edu;

import java.nio.charset.StandardCharsets;

import org.apache.servicecomb.springboot.starter.provider.EnableServiceComb;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfigurer;

import com.server.edu.dictionary.utils.SpringUtils;

import tk.mybatis.spring.annotation.MapperScan;

@SpringBootApplication
@Import(SpringUtils.class)
@MapperScan({"com.server.edu.*.dao","com.server.edu.*.*.dao"})
@EnableServiceComb
@EnableScheduling
public class ServiceApplication
{
    @Bean
    public FreeMarkerConfigurer freemarkerConfig()
        throws Exception
    {
        FreeMarkerConfigurer configurer = new FreeMarkerConfigurer();
        configurer.setTemplateLoaderPath("classpath:templates");
        configurer.setDefaultEncoding(StandardCharsets.UTF_8.name());
        return configurer;
    }
    
    public static void main(String[] args)
    {
        SpringApplication.run(ServiceApplication.class, args);
    }
    
}
