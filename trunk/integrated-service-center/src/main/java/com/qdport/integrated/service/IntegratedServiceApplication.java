package com.qdport.integrated.service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.web.client.RestTemplate;

@SpringBootApplication(scanBasePackages = {"com.qdport.*"}, exclude = {DataSourceAutoConfiguration.class})
@EnableDiscoveryClient
@EnableAspectJAutoProxy
public class IntegratedServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(IntegratedServiceApplication.class, args);
    }

    @Bean
    @LoadBalanced
    RestTemplate restTemplate() {
        return new RestTemplate();
    }
}
