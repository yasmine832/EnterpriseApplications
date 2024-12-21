package com.ea.enterpriseapplications;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@ComponentScan(basePackages = {"controller", "config", "repository", "entity", "service"})
@EntityScan(basePackages = {"entity"})
@EnableJpaRepositories(basePackages = {"repository"})
public class EnterpriseApplicationsApplication {

    public static void main(String[] args) {
        SpringApplication.run(EnterpriseApplicationsApplication.class, args);
    }

}
