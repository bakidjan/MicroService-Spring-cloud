package com.diallo.gatewayproxy;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.ReactiveDiscoveryClient;
import org.springframework.cloud.gateway.discovery.DiscoveryClientRouteDefinitionLocator;
import org.springframework.cloud.gateway.discovery.DiscoveryLocatorProperties;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;


@SpringBootApplication
	public class GatewayProxyApplication {

	public static void main(String[] args) {
		SpringApplication.run(GatewayProxyApplication.class, args);
	}

	/**
	 * static routes definition
	 *sinon on peut definir les routes de manière dynamique avec  DiscoveryClientRouteDefinitionLocator
	 * @param builder
	 * @return
	 */

	//@Bean
	RouteLocator routeLocator(RouteLocatorBuilder builder){
		return builder.routes()
				/*
                .route(r->r.path("/inventory/**").uri("http://localhost:8081/inventories").id("r1"))
                .route(r->r.path("/customers/**").uri("http://localhost:8082/customers").id("r2")).build();
                */
				/**
				 * lb = load balancer pour passer le nom du microservice au lieu de son ip et port
				 */
				.route("path", r->r.path("/inventories/**").uri("lb://INVENTORY-SERVICE").id("r1"))
				.route("path", r->r.path("/customers/**").uri("lb://CUSTOMER-SERVICE").id("r2"))
				/*
                .route(r->r.path("/inventory/**").uri("lb://INVENTORY-SERVICE").id("r1"))
                .route(r->r.path("/customers/**").uri("lb://CUSTOMER-SERVICE").id("r2"))
                .route(r->r.path("/countries/**")
                        .filters(f->f
                        .addRequestHeader("x-rapidapi-host", "restcountries-v1.p.rapidapi.com")
                        .addRequestHeader("x-rapidapi-key", "8e80debb4bmsh7b5c93af27fa043p1de728jsna831df93bf7f")
                        .rewritePath("/countries/(?<segment>.*)", "/${segment}")
                        )
                        .uri("https://restcountries-v1.p.rapidapi.com").id("r3"))
                        */
				.build();
	}



	@Bean
		DiscoveryClientRouteDefinitionLocator dynamicRoutes (
				ReactiveDiscoveryClient rdc, DiscoveryLocatorProperties dlp){
			return new DiscoveryClientRouteDefinitionLocator(rdc, dlp);
		}

	}