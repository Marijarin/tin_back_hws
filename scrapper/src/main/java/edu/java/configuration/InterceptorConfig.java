package edu.java.configuration;

import edu.java.service.util.IpInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class InterceptorConfig implements WebMvcConfigurer {

    private final IpInterceptor ipInterceptor;

    @Autowired
    public InterceptorConfig(IpInterceptor ipInterceptor) {
        this.ipInterceptor = ipInterceptor;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(ipInterceptor).addPathPatterns("/tg-chat/{id}", "/links");
    }
}
