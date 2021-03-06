package com.rw.carriages;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.web.filter.ShallowEtagHeaderFilter;

import javax.servlet.Filter;

@SpringBootApplication
public class CarriagesApplication {
    public static void main(String[] args) {
        SpringApplication.run(CarriagesApplication.class, args);
    }

    @Bean
    public FilterRegistrationBean shallowEtagBean() {
        FilterRegistrationBean frb = new FilterRegistrationBean();
        frb.setFilter(new ShallowEtagHeaderFilter());
        frb.addUrlPatterns("/");
        frb.setOrder(2);
        return frb;
    }
}
