package com.diallo.customer;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import lombok.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.data.rest.core.config.Projection;
import org.springframework.data.rest.core.config.RepositoryRestConfiguration;

	@Entity @Data @AllArgsConstructor @ToString @NoArgsConstructor
	class Customer {
		
		@Id @GeneratedValue(strategy = GenerationType.AUTO)
		private Long id;
		private String name;
		private String address;
		private String email;
		
	}
	@RepositoryRestResource
	interface CustomerRepository extends JpaRepository<Customer, Long>{};

/**
 * La projection permet de de personaliser le contenu qu'on veut afficher dans un classe
 */
@Projection(name="p1", types = Customer.class)
	interface customerProjection{
		public Long getId();
		public String getName();
		public String getAddress();
	}
	
	@SpringBootApplication
	public class CustomerServiceApplication {
		
	
		public static void main(String[] args) {
			SpringApplication.run(CustomerServiceApplication.class, args);
		}

		/**
		 *
		 * @param customerRepository
		 * @param restConfiguration pour exposer id dans l'API
		 * @return
		 */
		@Bean
		CommandLineRunner start(CustomerRepository customerRepository, RepositoryRestConfiguration restConfiguration) {
			return args -> {
				restConfiguration.exposeIdsFor(Customer.class);
				customerRepository.save(new Customer(null,"Ngolo", "Chez Zan", "ngolo@gmail.com"));
				customerRepository.save(new Customer(null,"Ndji", "Chez Zandjin", "ndji@gmail.com"));
				customerRepository.save(new Customer(null,"Namakoro", "Chez namakoro", "namakoro@gmail.com"));
				customerRepository.save(new Customer(null,"Mpieh", "Chez mpieh", "mpieh@gmail.com"));
				customerRepository.findAll().forEach(System.out::println);
			
			};
		}
	
	}